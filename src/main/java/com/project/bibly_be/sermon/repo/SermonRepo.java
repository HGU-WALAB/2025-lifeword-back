package com.project.bibly_be.sermon.repo;

import com.project.bibly_be.sermon.entity.Sermon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SermonRepo extends JpaRepository<Sermon, Long> {
}
