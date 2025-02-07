package com.project.bibly_be.bookmark.dto;

import com.project.bibly_be.bookmark.entity.Bookmark;
import com.project.bibly_be.sermon.entity.Content;
import com.project.bibly_be.sermon.entity.Sermon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class BookmarkResponseDTO {
    private Long bookmarkId;
    private LocalDateTime createdAt;

    // Bible 부분
    private Long verseId;
    private String sentence;
    private String testament;
    private Integer book;
    private Integer chapter;
    private Integer paragraph;
    private String longLabel;
    private String shortLabel;

    // Sermon 부분
    private Long sermonId;
    private LocalDateTime sermonDate;
    private LocalDateTime sermonCreatedAt;
    private LocalDateTime sermonUpdatedAt;
    private boolean isPublic;
    private String worshipType;
    private String mainScripture;
    private String additionalScripture;
    private String sermonTitle;
    private String summary;
    private String notes;
    private String recordInfo;
    private String fileCode;
    private List<String> contents;

    public static BookmarkResponseDTO from(Bookmark bookmark) {
        if (bookmark.getIsSermon()) {
            // 설교 데이터일 경우 Sermon 정보와 Content 리스트 추가
            Sermon sermon = bookmark.getSermon();
            return BookmarkResponseDTO.builder()
                    .bookmarkId(bookmark.getId())
                    .sermonId(sermon.getSermonId())
                    .sermonDate(sermon.getSermonDate())
                    .sermonCreatedAt(sermon.getCreatedAt())
                    .sermonUpdatedAt(sermon.getUpdatedAt())
                    .isPublic(sermon.isPublic())
                    .worshipType(sermon.getWorshipType())
                    .mainScripture(sermon.getMainScripture())
                    .additionalScripture(sermon.getAdditionalScripture())
                    .sermonTitle(sermon.getSermonTitle())
                    .summary(sermon.getSummary())
                    .notes(sermon.getNotes())
                    .recordInfo(sermon.getRecordInfo())
                    .fileCode(sermon.getFileCode())
                    .contents(sermon.getContents().stream()
                            .map(Content::getContentText)
                            .collect(Collectors.toList()))
                    .createdAt(bookmark.getCreatedAt())
                    .build();
        } else {
            // 성경 구절 데이터일 경우 Bible 정보를 추가
            return BookmarkResponseDTO.builder()
                    .bookmarkId(bookmark.getId())
                    .verseId(bookmark.getVerse().getIdx())
                    .sentence(bookmark.getVerse().getSentence())
                    .testament(bookmark.getVerse().getTestament())
                    .book(bookmark.getVerse().getBook())
                    .chapter(bookmark.getVerse().getChapter())
                    .paragraph(bookmark.getVerse().getParagraph())
                    .longLabel(bookmark.getVerse().getLongLabel())
                    .shortLabel(bookmark.getVerse().getShortLabel())
                    .createdAt(bookmark.getCreatedAt())
                    .build();
        }
    }
}
