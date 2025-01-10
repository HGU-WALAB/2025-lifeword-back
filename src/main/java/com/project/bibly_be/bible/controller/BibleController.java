package com.project.bibly_be.bible.controller;

import com.project.bibly_be.bible.dto.ApiResponseDTO;
import com.project.bibly_be.bible.service.BibleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BibleController {
    private final BibleService bibleService;
   /* @GetMapping("/bibles")
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
    }*/

    @GetMapping("/bibles/search")
    public ApiResponseDTO<?> search(

            @RequestParam(required = false) String keyword1) {

        List<?> results = bibleService.search(keyword1);
        return ApiResponseDTO.success("조회 완료", results);
    }
}