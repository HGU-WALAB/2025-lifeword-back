package com.project.bibly_be.sermon.dto;

import com.project.bibly_be.sermon.entity.Sermon;
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
    //bookmarked (if sermon.userId == userId )-> true else false;
    private boolean bookmarked;
    private Long textCount;

    public static SermonResponseDTO from(Sermon sermon, boolean bookmarked, Long textCount) {
        return SermonResponseDTO.builder()
                .sermonId(sermon.getSermonId())
                .ownerName(sermon.getOwner().getName())
                .sermonDate(sermon.getSermonDate())
                .createdAt(sermon.getCreatedAt())
                .updatedAt(sermon.getUpdatedAt())
                .isPublic(sermon.isPublic())
                .worshipType(sermon.getWorshipType())
                .mainScripture(sermon.getMainScripture())
                .additionalScripture(sermon.getAdditionalScripture())
                .sermonTitle(sermon.getSermonTitle())
                .summary(sermon.getSummary())
                .notes(sermon.getNotes())
                .recordInfo(sermon.getRecordInfo())
                .fileCode(sermon.getFileCode())
                .bookmarked(bookmarked)
                .textCount(textCount)
                .build();
    }

}