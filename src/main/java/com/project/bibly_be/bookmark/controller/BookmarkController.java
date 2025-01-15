package com.project.bibly_be.bookmark.controller;

import com.project.bibly_be.bookmark.dto.BookmarkRequestDTO;
import com.project.bibly_be.bible.dto.ApiResponseDTO;
import com.project.bibly_be.bookmark.dto.BookmarkResponseDTO;
import com.project.bibly_be.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    public ApiResponseDTO<BookmarkResponseDTO> createBookmark(
            @RequestParam("userID") UUID userId,  // 헤더에서 파라미터로 변경
            @RequestBody BookmarkRequestDTO request) {
        BookmarkResponseDTO bookmark = bookmarkService.createBookmark(userId, request);
        return ApiResponseDTO.success("북마크 생성 완료", bookmark);
    }

    @GetMapping
    public ApiResponseDTO<List<BookmarkResponseDTO>> getBookmarks(
            @RequestParam("userID") UUID userId) {  // 헤더에서 파라미터로 변경
        List<BookmarkResponseDTO> bookmarks = bookmarkService.getBookmarks(userId);
        return ApiResponseDTO.success("북마크 목록 조회 완료", bookmarks);
    }

    @DeleteMapping("/{verseId}")
    public ApiResponseDTO<Void> deleteBookmark(
            @RequestParam("userID") UUID userId,  // 헤더에서 파라미터로 변경
            @PathVariable Long verseId) {
        bookmarkService.deleteBookmark(userId, verseId);
        return ApiResponseDTO.success("북마크 삭제 완료", null);
    }
}