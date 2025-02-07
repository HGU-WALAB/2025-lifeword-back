package com.project.bibly_be.bookmark.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookmarkRequestDTO {
    private Long verseId;
    private Long sermonId;
    private Boolean isSermon;
}