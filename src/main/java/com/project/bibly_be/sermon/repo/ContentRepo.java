package com.project.bibly_be.sermon.repo;

import com.project.bibly_be.sermon.entity.Content;
import com.project.bibly_be.sermon.entity.Sermon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepo extends JpaRepository<Content, Long> {
}