package com.project.bibly_be.sermon.repository;

import com.project.bibly_be.sermon.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {


}