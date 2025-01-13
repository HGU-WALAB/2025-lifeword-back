package com.project.bibly_be.sermon.entity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @OneToOne
    @JoinColumn(name = "sermon_id", referencedColumnName = "sermonId")
    private Sermon sermon;

    @Lob
    private String contentText; // Full HTML content

    private LocalDateTime contentCreatedAt = LocalDateTime.now();
    private LocalDateTime contentUpdatedAt = LocalDateTime.now();
}
