package com.project.bibly_be.text.repository;

import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.text.entity.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TextRepository extends JpaRepository<Text, Long> {

    @Query("SELECT t FROM Text t WHERE t.sermon.sermonId = :sermonId AND " +
            "(t.isDraft = false OR (t.isDraft = true AND t.user.id = :userId))")
    // visibility 가 draft ye no 이여
    List<Text> findBySermonIdAndVisibility(@Param("sermonId") Long sermonId, @Param("userId") UUID userId);

    //Long countBySermon_SermonIdAndIsDraft(Long sermonId, Boolean isDraft);
    @Query("SELECT COUNT(t) FROM Text t WHERE t.sermon.sermonId = :sermonId AND (t.isDraft = false OR (t.isDraft = true AND t.user.id = :userId))")
    Long countBySermonIdWithIsDraft(@Param("sermonId") Long sermonId, @Param("userId") UUID userId);


    @Query("SELECT count(t) from Text t where t.sermon.sermonId = :sermonId")
    Long countBySermonId(@Param("sermonId") Long sermonId);

    List<Text> findBySermon_SermonId(Long sermonSermonId);
    //Long countByIdIsWithinAndDraft(Long id, boolean draft);


    // draft = false only
    @Query("SELECT DISTINCT t.sermon FROM Text t " +
            "WHERE t.isDraft = false " +
            "AND (t.textTitle LIKE CONCAT('%', :keyword, '%') OR t.textContent LIKE CONCAT('%', :keyword, '%'))")
    List<Sermon> findSermonByKeyword(@Param("keyword") String keyword);

    @Query("SELECT t FROM Text t " +
            "WHERE t.id = (SELECT MIN(tt.id) FROM Text tt " +
            "WHERE tt.sermon.sermonId = :sermonId AND tt.isDraft = false)")
    Text findTextContent(@Param("sermonId") Long sermonId);


}
