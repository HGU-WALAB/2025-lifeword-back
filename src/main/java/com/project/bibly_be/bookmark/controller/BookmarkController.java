package com.project.bibly_be.bookmark.controller;

import com.project.bibly_be.bookmark.dto.BookmarkRequestDTO;
import com.project.bibly_be.bible.dto.ApiResponseDTO;
import com.project.bibly_be.bookmark.dto.BookmarkResponseDTO;
import com.project.bibly_be.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    public ApiResponseDTO<BookmarkResponseDTO> createBookmark(
            @RequestParam("kakaoUid") String kakaoUid,  // 헤더에서 파라미터로 변경
            @RequestBody BookmarkRequestDTO request) {
        BookmarkResponseDTO bookmark = bookmarkService.createBookmark(kakaoUid, request);
        return ApiResponseDTO.success("북마크 생성 완료", bookmark);
    }

    @GetMapping
    public ApiResponseDTO<List<BookmarkResponseDTO>> getBookmarks(
            @RequestParam("kakaoUid") String kakaoUid) {  // 헤더에서 파라미터로 변경
        List<BookmarkResponseDTO> bookmarks = bookmarkService.getBookmarks(kakaoUid);
        return ApiResponseDTO.success("북마크 목록 조회 완료", bookmarks);
    }

    @DeleteMapping("/{verseId}")
    public ApiResponseDTO<Void> deleteBookmark(
            @RequestParam("kakaoUid") String kakaoUid,  // 헤더에서 파라미터로 변경
            @PathVariable Long verseId) {
        bookmarkService.deleteBookmark(kakaoUid, verseId);
        return ApiResponseDTO.success("북마크 삭제 완료", null);
    }
}