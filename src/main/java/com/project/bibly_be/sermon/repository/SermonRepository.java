package com.project.bibly_be.sermon.repository;

import com.project.bibly_be.sermon.entity.Sermon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SermonRepository extends JpaRepository<Sermon, Long> {

    List<Sermon> findByIsPublicTrue();
    boolean existsByFileCode(String fileCode);

<<<<<<< HEAD
    List<Sermon> findByOwner_IdAndIsPublicFalse(UUID ownerId);

    List<Sermon> findByOwner_Id(UUID ownerId);

=======
>>>>>>> 066abe223372d9dd3cd4c307157024999760d034

}