package com.project.bibly_be.sermon.entity;

import com.project.bibly_be.user.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Sermon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sermonId;

    @Column(nullable = false)
    private UUID sermonOwnerId; // User UUID as a foreign key

    @Column(nullable = false)
    private String sermonOwner; // User's name

    @Column(nullable = false)
    private Boolean isPublic;

    private String worshipType;
    private String mainScripture;
    private String additionalScripture;
    private String sermonTitle;
    private String summary;
    private String notes;
    private String recordInfo;
    private String fileCode;

    private LocalDateTime sermonCreatedAt = LocalDateTime.now();
    private LocalDateTime sermonUpdatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "sermon", cascade = CascadeType.ALL)
    private Content content;
}
