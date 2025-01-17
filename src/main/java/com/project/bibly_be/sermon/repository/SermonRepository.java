package com.project.bibly_be.sermon.repository;

import com.project.bibly_be.sermon.entity.Sermon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SermonRepository extends JpaRepository<Sermon, Long> {

    List<Sermon> findByIsPublicTrue();

    boolean existsByFileCode(String fileCode);

    // GET USER semormon

        List<Sermon> findByOwner_Id(UUID ownerId);
        List<Sermon> findByOwner_IdAndIsPublicFalse(UUID ownerId);
        List<Sermon> findByOwner_IdAndIsPublicTrue(UUID ownerId);



    // Search private sermons by title for a specific user
    @Query("SELECT s FROM Sermon s WHERE LOWER(s.sermonTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) AND s.owner.id = :userId ORDER BY s.sermonId DESC")
    List<Sermon> searchBySermonTitle(@Param("keyword") String keyword, @Param("userId") UUID userId);

    // Search private sermons by title or content for a specific user
    @Query("SELECT DISTINCT s FROM Sermon s LEFT JOIN s.contents c WHERE " +
            "(LOWER(s.sermonTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.contentText) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND s.owner.id = :userId ORDER BY s.sermonId DESC")
    List<Sermon> searchBySermonTitleOrContent(@Param("keyword") String keyword, @Param("userId") UUID userId);
}