package com.project.bibly_be.text.repository;

import com.project.bibly_be.text.entity.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TextRepository extends JpaRepository<Text, Long> {

}
