package com.project.bibly_be.sermon.controller;

import com.project.bibly_be.sermon.dto.SermonRequestDTO;
import com.project.bibly_be.sermon.dto.SermonResponseDTO;
import com.project.bibly_be.sermon.service.SermonService;
import com.project.bibly_be.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sermons")
@RequiredArgsConstructor
public class SermonController {

    private final SermonService sermonService;
    private final UserRepository userRepository;



    // Get all public sermons
    @Operation(summary = "public 되어있는 설교 다 불러오기 룰루 ( SermonPublicList) ")
    @GetMapping("/publiclist")
    public List<SermonResponseDTO> getAllPublicSermons() {
        return sermonService.getAllPublicSermons();
    }

    // Create a new sermon
    @Operation(summary = "설교 추가 ( AddSermon ) ")
    @PostMapping
    public SermonResponseDTO createSermon(@RequestBody SermonRequestDTO requestDTO) {
        return sermonService.createSermon(requestDTO);
    }

    // Update a sermon (PATCH)
    @Operation(summary = "설교 수정띠 , 로그인된  ID 보내 주시면 비교해서 업뎃해줌( UpdateSermon ) ")
    @PatchMapping("/{sermonId}")
    public SermonResponseDTO updateSermon(@PathVariable Long sermonId,
                                          @RequestBody SermonRequestDTO requestDTO,
                                          @RequestParam("userId") String loggedInUserId) {
        return sermonService.updateSermon(sermonId, requestDTO, loggedInUserId);
    }


    // Delete a sermon
    @Operation(summary = "설교 삭제. 낄낄 PATCH 과 같이 로그인된 유저 아디도 보내주셈( DeleteSermon ) ")
    @DeleteMapping("/{sermonId}")
    public void deleteSermon(@PathVariable Long sermonId,
                             @RequestParam("userId") String loggedInUserId) {
        sermonService.deleteSermon(sermonId, loggedInUserId);
    }


}
