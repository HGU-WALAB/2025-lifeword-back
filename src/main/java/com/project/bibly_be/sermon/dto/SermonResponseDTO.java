package com.project.bibly_be.sermon.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class SermonResponseDTO {
    private Long id;
    private String owner;
    private String title;
    private LocalDate sermonDate;
    private List<Long> bibleVerseIndexes;
    private String sermonContent;
    private List<String> keywords;
}

