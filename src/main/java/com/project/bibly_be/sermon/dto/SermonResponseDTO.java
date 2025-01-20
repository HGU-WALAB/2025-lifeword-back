package com.project.bibly_be.sermon.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class SermonResponseDTO {
    private Long sermonId;
    private String ownerName;
    private UUID userId; //UUID 추가
    private LocalDateTime sermonDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isPublic;
    private String worshipType;
    private String mainScripture;
    private String additionalScripture;
    private String sermonTitle;
    private String summary;
    private String notes;
    private String recordInfo;
    private String fileCode;
    private List<ContentDTO> contents;
}