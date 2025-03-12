package com.project.bibly_be.sermon.repository;

import com.project.bibly_be.sermon.entity.Sermon;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SermonRepository extends JpaRepository<Sermon, Long>, JpaSpecificationExecutor<Sermon> {

    List<Sermon> findByIsPublicTrue();

    boolean existsByFileCode(String fileCode);

    // GET USER semormon

        List<Sermon> findByOwner_Id(UUID ownerId);
        List<Sermon> findByOwner_IdAndIsPublicFalse(UUID ownerId);
        List<Sermon> findByOwner_IdAndIsPublicTrue(UUID ownerId);

        // user Id 까지
    @Query("SELECT s FROM Sermon s JOIN FETCH s.owner")
    List<Sermon> findAllWithOwner();

    // 작성자(User) 이름으로 설교 검색 (새로운 쿼리)
    @Query("SELECT s FROM Sermon s WHERE LOWER(s.owner.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY s.sermonId DESC")
    List<Sermon> searchByAuthorName(@Param("keyword") String keyword);

    // 제목으로 설교 검색
    @Query("SELECT s FROM Sermon s WHERE LOWER(s.sermonTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY s.sermonId DESC")
    List<Sermon> searchBySermonTitle(@Param("keyword") String keyword);

    /*
    @Query("SELECT DISTINCT s FROM Sermon s LEFT JOIN s.contents c WHERE " +
            "(LOWER(s.sermonTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.contentText) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY s.sermonId DESC")
    List<Sermon> searchBySermonTitleOrContent(@Param("keyword") String keyword);
    */



}