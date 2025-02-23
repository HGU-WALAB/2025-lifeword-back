package com.project.bibly_be.bookmark.repository;

import com.project.bibly_be.bookmark.entity.Bookmark;
import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUser(User user);
    List<Bookmark> findAll();
    Bookmark findByUserAndVerseIdx(User user, Long verseId);

    Bookmark findByUserAndSermon_SermonId(User user, Long sermonId);
    void deleteByIdAndUser(Long bookmarkId, User user);

    @Query("SELECT b FROM Bookmark b " +
            "LEFT JOIN FETCH b.sermon s " +
            "LEFT JOIN FETCH s.contents " +
            "WHERE b.user = :user")
    List<Bookmark> findAllByUser(@Param("user") User user);

    @Query("SELECT b FROM Bookmark b " +
            "LEFT JOIN FETCH b.sermon s " +
            "LEFT JOIN FETCH s.contents " +
            "WHERE b.user = :user AND b.isSermon = true")
    List<Bookmark> findAllWithSermonContentsByUser(@Param("user") User user);


    @Query("SELECT b FROM Bookmark b " +
            "WHERE b.user = :user AND b.isSermon = false")
    List<Bookmark> findAllVerseByUser(@Param("user") User user);

    Boolean existsByUserAndSermon(User user, Sermon sermon);

}