package com.project.bibly_be.sermon.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SermonRequestDto {
    private UUID sermonOwnerId;
    private String sermonTitle;
    private String summary;
    private String worshipType;
    private String mainScripture;
    private String additionalScripture;
    private String notes;
    private String recordInfo;
    private String fileCode;
    private Boolean isPublic;
    private String contentText;
}