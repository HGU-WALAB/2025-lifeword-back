package com.project.bibly_be.sermon.repository;

import com.project.bibly_be.sermon.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    // SEARCH content
    @Query("SELECT c FROM Content c WHERE LOWER(c.contentText) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY c.sermon.sermonId DESC")
    List<Content> searchByContentText(@Param("keyword") String keyword);
}