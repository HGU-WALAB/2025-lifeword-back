package com.project.bibly_be.sermon.repository;

import com.project.bibly_be.sermon.entity.Sermon;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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

        // user Id 까지
    @Query("SELECT s FROM Sermon s JOIN FETCH s.owner")
    List<Sermon> findAllWithOwner();

    // 작성자(User) 이름으로 설교 검색 (새로운 쿼리)
    @Query("SELECT s FROM Sermon s WHERE LOWER(s.owner.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY s.sermonId DESC")
    List<Sermon> searchByAuthorName(@Param("keyword") String keyword);

    // 제목으로 설교 검색
    @Query("SELECT s FROM Sermon s WHERE LOWER(s.sermonTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY s.sermonId DESC")
    List<Sermon> searchBySermonTitle(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT s FROM Sermon s LEFT JOIN s.contents c WHERE " +
            "(LOWER(s.sermonTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.contentText) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY s.sermonId DESC")
    List<Sermon> searchBySermonTitleOrContent(@Param("keyword") String keyword);

    // Search private sermons by title for a specific user
   /* @Query("SELECT s FROM Sermon s WHERE LOWER(s.sermonTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) AND s.owner.id = :userId ORDER BY s.sermonId DESC")
    List<Sermon> searchBySermonTitle(@Param("keyword") String keyword, @Param("userId") UUID userId);

    // Search private sermons by title or content for a specific user
    @Query("SELECT DISTINCT s FROM Sermon s LEFT JOIN s.contents c WHERE " +
            "(LOWER(s.sermonTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.contentText) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND s.owner.id = :userId ORDER BY s.sermonId DESC")

    List<Sermon> searchBySermonTitleOrContent(@Param("keyword") String keyword, @Param("userId") UUID userId);

    @Query("SELECT s FROM Sermon s WHERE LOWER(s.worshipType) = LOWER(:worshipType)")
    List<Sermon> findByWorshipType(@Param("worshipType") String worshipType);*/

    @Query("SELECT s FROM Sermon s WHERE " +
            "(:worshipType IS NULL OR s.worshipType = :worshipType) " +
            "AND (:startDate IS NULL OR s.sermonDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.sermonDate <= :endDate) " +
            "AND (:scripture IS NULL OR LOWER(s.mainScripture) LIKE LOWER(CONCAT('%', :scripture, '%')) " +
            "OR LOWER(s.additionalScripture) LIKE LOWER(CONCAT('%', :scripture, '%'))) " +
            "ORDER BY s.sermonDate DESC")
    List<Sermon> findFilteredSermons(
            @Param("worshipType") String worshipType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("scripture") String scripture
    );

    @Query("SELECT s FROM Sermon s WHERE " +
            "(:worshipType IS NULL OR s.worshipType = :worshipType) " +
            "AND (:startDate IS NULL OR s.sermonDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.sermonDate <= :endDate) " +
            "AND (:scripture IS NULL OR LOWER(s.mainScripture) LIKE LOWER(CONCAT('%', :scripture, '%')) " +
            "OR LOWER(s.additionalScripture) LIKE LOWER(CONCAT('%', :scripture, '%'))) " +
            "ORDER BY " +
            "CASE WHEN :sortOrder = 'asc' THEN s.sermonDate END ASC, " +
            "CASE WHEN :sortOrder = 'desc' THEN s.sermonDate END DESC, " +
            "CASE WHEN :sortOrder = 'recent' THEN s.updatedAt END DESC")
    Page<Sermon> findFilteredSermonsPage(
            @Param("worshipType") String worshipType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("scripture") String scripture,
            @Param("sortOrder") String sortOrder,
            Pageable pageable
    );

    @Query("SELECT s FROM Sermon s WHERE " +
            "(:worshipType IS NULL OR s.worshipType = :worshipType) " +
            "AND (:startDate IS NULL OR s.sermonDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.sermonDate <= :endDate) " +
            "AND (:scripture IS NULL OR " +
            "    (LOWER(s.mainScripture) LIKE LOWER(CONCAT('%', :scripture, '%')) " +
            "     OR LOWER(s.additionalScripture) LIKE LOWER(CONCAT('%', :scripture, '%')))) " +
            "AND (" +
            "    (:mode = 0 AND s.isPublic = true) " +
            " OR (:mode = 1 AND s.owner.id = :userId) " +
            " OR (:mode = 2 AND ( s.owner.id = :userId AND s.isPublic = true)) " +
            " OR (:mode = 3 AND (s.owner.id = :userId AND s.isPublic = false ))" +
            ") " +
            "ORDER BY " +
            "CASE WHEN :sortOrder = 'asc' THEN s.sermonDate END ASC, " +
            "CASE WHEN :sortOrder = 'desc' THEN s.sermonDate END DESC, " +
            "CASE WHEN :sortOrder = 'recent' THEN s.updatedAt END DESC")
    Page<Sermon> findFilteredSermonsDynamic(
            @Param("worshipType") String worshipType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("scripture") String scripture,
            @Param("sortOrder") String sortOrder,
            @Param("mode") int mode,
            @Param("userId") UUID userId,
            Pageable pageable
    );

    // 모드 0: 공개된 설교만
    @Query("SELECT s FROM Sermon s " +
            "WHERE s.isPublic = true " +
           "AND (:worshipType IS NULL OR s.worshipType = :worshipType) " +
            "AND (:startDate IS NULL OR s.sermonDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.sermonDate <= :endDate) " +
            "AND (:scripture IS NULL OR " +
            "    (LOWER(s.mainScripture) LIKE LOWER(CONCAT('%', :scripture, '%')) " +
            "     OR LOWER(s.additionalScripture) LIKE LOWER(CONCAT('%', :scripture, '%'))))")
    Page<Sermon> findPublicSermons(
            @Param("worshipType") String worshipType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("scripture") String scripture,
            Pageable pageable
    );



    // 모드 1: 해당 사용자의 모든 설교
    @Query("SELECT s FROM Sermon s " +
            "WHERE s.owner.id = :userId " +
            "AND (:worshipType IS NULL OR s.worshipType = :worshipType) " +
            "AND (:startDate IS NULL OR s.sermonDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.sermonDate <= :endDate) " +
            "AND (:scripture IS NULL OR " +
            "    (LOWER(s.mainScripture) LIKE LOWER(CONCAT('%', :scripture, '%')) " +
            "     OR LOWER(s.additionalScripture) LIKE LOWER(CONCAT('%', :scripture, '%'))))")

    Page<Sermon> findUserSermons(
            @Param("userId") UUID userId,
            @Param("worshipType") String worshipType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("scripture") String scripture,
            Pageable pageable
    );

    // 모드 2: 공개 + 해당 사용자의 설교 (둘 다 만족; 실제 기획에 따라 OR 로 바꾸는지 AND 로 바꾸는지 조정)
    @Query("SELECT s FROM Sermon s " +
            "WHERE s.isPublic = true AND s.owner.id = :userId " +
            "AND (:worshipType IS NULL OR s.worshipType = :worshipType) " +
            "AND (:startDate IS NULL OR s.sermonDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.sermonDate <= :endDate) " +
            "AND (:scripture IS NULL OR " +
            "    (LOWER(s.mainScripture) LIKE LOWER(CONCAT('%', :scripture, '%')) " +
            "     OR LOWER(s.additionalScripture) LIKE LOWER(CONCAT('%', :scripture, '%'))))")

    Page<Sermon> findPublicUserSermons(
            @Param("userId") UUID userId,
            @Param("worshipType") String worshipType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("scripture") String scripture,
            Pageable pageable
    );

    // 모드 3: 비공개 + 해당 사용자의 설교
    @Query("SELECT s FROM Sermon s " +
            "WHERE s.isPublic = false AND s.owner.id = :userId " +
            "AND (:worshipType IS NULL OR s.worshipType = :worshipType) " +
            "AND (:startDate IS NULL OR s.sermonDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.sermonDate <= :endDate) " +
            "AND (:scripture IS NULL OR " +
            "    (LOWER(s.mainScripture) LIKE LOWER(CONCAT('%', :scripture, '%')) " +
            "     OR LOWER(s.additionalScripture) LIKE LOWER(CONCAT('%', :scripture, '%'))))")

    Page<Sermon> findPrivateUserSermons(
            @Param("userId") UUID userId,
            @Param("worshipType") String worshipType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("scripture") String scripture,

            Pageable pageable
    );










}