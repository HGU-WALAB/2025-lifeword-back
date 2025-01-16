package com.project.bibly_be.sermon.repository;

import com.project.bibly_be.sermon.entity.Sermon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SermonRepository extends JpaRepository<Sermon, Long> {

    List<Sermon> findByIsPublicTrue();
    boolean existsByFileCode(String fileCode);
    List<Sermon> findByOwner_IdAndIsPublicFalse(String ownerId);


}