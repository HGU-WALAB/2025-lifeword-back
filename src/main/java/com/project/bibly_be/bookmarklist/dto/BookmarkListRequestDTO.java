package com.project.bibly_be.bookmarklist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor

public class BookmarkListRequestDTO {
    private UUID userId;
    private String name;
    private List<Long> verseIds;
    private List<Long> sermonIds;

}
