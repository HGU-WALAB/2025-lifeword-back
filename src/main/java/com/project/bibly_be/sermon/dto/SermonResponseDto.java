package com.project.bibly_be.sermon.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SermonResponseDto {
    private Long sermonId;
    private UUID sermonOwnerId;
    private String sermonOwner;
    private Boolean isPublic;

    private String worshipType;
    private String mainScripture;
    private String additionalScripture;
    private String sermonTitle;
    private String summary;
    private String notes;
    private String recordInfo;
    private String fileCode;

    private LocalDateTime sermonCreatedAt;
    private LocalDateTime sermonUpdatedAt;

    private String contentText;
}
