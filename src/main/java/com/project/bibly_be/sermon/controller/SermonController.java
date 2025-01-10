package com.project.bibly_be.sermon.controller;


import com.project.bibly_be.bible.dto.ApiResponseDTO;
import com.project.bibly_be.sermon.dto.SermonRequestDTO;
import com.project.bibly_be.sermon.dto.SermonResponseDTO;
import com.project.bibly_be.sermon.service.SermonService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sermons")
@RequiredArgsConstructor
public class SermonController {
    private final SermonService sermonService;

    @GetMapping
    @Operation(summary = "설교들 리스트 받아오기!!!! ( SermonList )", description = "아직 설교 성경 구절은 ? 인텍스만 ..")
    public ApiResponseDTO<List<SermonResponseDTO>> getAllSermons() {
        List<SermonResponseDTO> sermons = sermonService.getAllSermons();
        return ApiResponseDTO.success(
                sermons.isEmpty() ? "설교 없고" : "설교 리스트 잘 받아왔고",
                sermons
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "선택한 설교 디테일 불러오기 룰루 ( SermonDetail )", description = " ㅋㅋ 예")
    public ApiResponseDTO<SermonResponseDTO> getSermonDetails(@PathVariable Long id) {
        SermonResponseDTO sermon = sermonService.getSermonDetails(id);
        return ApiResponseDTO.success("선택한 설교 내용 잘 받아왔고", sermon);
    }

    @PostMapping
    @Operation(summary = "설교 추가하기 ( AddSermon )", description = "json 으로 받긴 하는데 원하는 형태 있나유?")
    public ApiResponseDTO<Void> addSermon(@RequestBody SermonRequestDTO requestDTO) {
        sermonService.addSermon(requestDTO);
        return ApiResponseDTO.success("설교 잘 추가했고", null);
    }
}

