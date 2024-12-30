package com.project.bibly_be.repository;

import com.project.bibly_be.entity.Bookmark;
import com.project.bibly_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUser(User user);
    Optional<Bookmark> findByUserAndVerseIdx(User user, Long verseId);
    void deleteByUserAndVerseIdx(User user, Long verseId);
}