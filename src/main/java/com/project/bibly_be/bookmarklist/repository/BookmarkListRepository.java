package com.project.bibly_be.bookmarklist.repository;

import com.project.bibly_be.bookmarklist.entity.BookmarkList;
import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkListRepository extends JpaRepository<BookmarkList, Long> {

    public List<BookmarkList> findByUserId(UUID userId);

    UUID user(User user);
}
