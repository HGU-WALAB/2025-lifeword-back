package com.project.bibly_be.dto.response;

import com.project.bibly_be.entity.Bookmark;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookmarkResponseDTO {
    private Long bookmark_id;
    private Long verse_id;
    private String sentence;
    private String testament;
    private Integer book;
    private Integer chapter;
    private Integer paragraph;
    private String long_label;
    private String short_label;
    private LocalDateTime created_at;

    public static BookmarkResponseDTO from(Bookmark bookmark) {
        return BookmarkResponseDTO.builder()
                .bookmark_id(bookmark.getId())
                .verse_id(bookmark.getVerse().getIdx())
                .sentence(bookmark.getVerse().getSentence())
                .testament(bookmark.getVerse().getTestament())
                .book(bookmark.getVerse().getBook())
                .chapter(bookmark.getVerse().getChapter())
                .paragraph(bookmark.getVerse().getParagraph())
                .long_label(bookmark.getVerse().getLongLabel())
                .short_label(bookmark.getVerse().getShortLabel())
                .created_at(bookmark.getCreatedAt())
                .build();
    }
}