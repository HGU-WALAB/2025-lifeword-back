package com.project.bibly_be.sermon.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class SermonRequestDTO {
    private String owner;
    private String title;
    private LocalDate sermonDate;
    private List<Long> bibleVerseIndexes;
    private String sermonContent;
    private List<String> keywords;
}

