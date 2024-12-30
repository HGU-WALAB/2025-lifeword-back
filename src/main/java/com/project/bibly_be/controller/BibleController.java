package com.project.bibly_be.controller;

import com.project.bibly_be.dto.response.ApiResponseDTO;
import com.project.bibly_be.dto.response.BibleResponseDTO;
import com.project.bibly_be.dto.response.BookResponseDTO;
import com.project.bibly_be.service.BibleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BibleController {
    private final BibleService bibleService;

    @GetMapping("/bibles")
    public ApiResponseDTO<List<BibleResponseDTO>> getBibles(
            @RequestParam String testament,
            @RequestParam(required = false) Integer book,
            @RequestParam(required = false) Integer chapter) {
        List<BibleResponseDTO> bibles = bibleService.getBibles(testament, book, chapter);
        return ApiResponseDTO.success("성경 구절 조회 완료", bibles);
    }

    @GetMapping("/books")
    public ApiResponseDTO<List<BookResponseDTO>> getBooks(@RequestParam String testament) {
        List<BookResponseDTO> books = bibleService.getBooks(testament);
        return ApiResponseDTO.success("성경 책 목록 조회 완료", books);
    }

    @GetMapping("/bibles/search")
    public ApiResponseDTO<List<BibleResponseDTO>> search(
            @RequestParam String mode,
            @RequestParam String keyword1,
            @RequestParam(required = false) String keyword2,
            @RequestParam(required = false) String operator) {
        List<BibleResponseDTO> searchResults = bibleService.search(mode, keyword1, keyword2, operator);
        return ApiResponseDTO.success("검색 결과 조회 완료", searchResults);
    }
}