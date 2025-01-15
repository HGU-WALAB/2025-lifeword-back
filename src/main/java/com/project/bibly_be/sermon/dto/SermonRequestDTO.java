package com.project.bibly_be.sermon.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SermonRequestDTO {
    private UUID userId;          // Required: User creating the sermon
    private String sermonDate;    // Required: Format yyyy-MM-dd
    private String worshipType;   // Required
    private String mainScripture; // Required
    private String additionalScripture; // Optional
    private String sermonTitle;   // Required
    private String summary;       // Optional
    private String notes;         // Optional
    private String recordInfo;    // Optional
    private String contentText;   // Required: The single content for the sermon
    private boolean isPublic;
}
