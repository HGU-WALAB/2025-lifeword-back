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
public class BookmarkListResponseDTO {
    private Long id;
    private String name;
    private List<SermonResponseDTO> sermons;
    private List<BibleResponseDTO> verses;
    private LocalDateTime createAt;

    public static BookmarkListResponseDTO from(BookmarkList bookmarkList, List<SermonResponseDTO> sermonDTOs, List<BibleResponseDTO> bibleDTOs) {
        return BookmarkListResponseDTO.builder()
                .id(bookmarkList.getId())
                .name(bookmarkList.getName())
                .sermons(sermonDTOs)
                .verses(bibleDTOs)
                .createAt(bookmarkList.getCreatedAt())
                .build();
    }

    public static BookmarkListResponseDTO sermon(BookmarkList bookmarkList, List<SermonResponseDTO> sermonDTOs) {
        return BookmarkListResponseDTO.builder()
                .id(bookmarkList.getId())
                .name(bookmarkList.getName())
                .sermons(sermonDTOs)
                .verses(null)
                .createAt(bookmarkList.getCreatedAt())
                .build();
    }

    public static BookmarkListResponseDTO bible(BookmarkList bookmarkList, List<BibleResponseDTO> verseResponseDTOList) {
        return BookmarkListResponseDTO.builder()
                .id(bookmarkList.getId())
                .name(bookmarkList.getName())
                .sermons(null)
                .verses(verseResponseDTOList)
                .createAt(bookmarkList.getCreatedAt())
                .build();
    }
}
