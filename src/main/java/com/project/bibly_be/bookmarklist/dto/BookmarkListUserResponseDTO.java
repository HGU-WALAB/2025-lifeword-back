package com.project.bibly_be.bookmarklist.dto;
import com.project.bibly_be.bible.dto.BibleResponseDTO;
import com.project.bibly_be.bookmarklist.entity.BookmarkList;
import com.project.bibly_be.sermon.dto.SermonResponseDTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BookmarkListUserResponseDTO {
    private Long id;
    private String name;

    public static BookmarkListUserResponseDTO from(BookmarkList bookmarkList) {
        return BookmarkListUserResponseDTO.builder()
                .id(bookmarkList.getId())
                .name(bookmarkList.getName())
                .build();
    }
}
