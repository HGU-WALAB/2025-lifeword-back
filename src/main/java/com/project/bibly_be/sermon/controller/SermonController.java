package com.project.bibly_be.sermon.controller;

import com.project.bibly_be.sermon.dto.SermonRequestDTO;
import com.project.bibly_be.sermon.dto.SermonResponseDTO;
import com.project.bibly_be.sermon.service.SermonService;
import com.project.bibly_be.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sermons")
@RequiredArgsConstructor
public class SermonController {

    private final SermonService sermonService;
    private final UserRepository userRepository;



    // GET all public sermons
    @Operation(summary = "public 되어있는 설교 다 불러오기 룰루 ( SermonPublicList ) ")
    @GetMapping("/publiclist")
    public List<SermonResponseDTO> getAllPublicSermons() {
        return sermonService.getAllPublicSermons();
    }

    @Operation(summary =  "Admin 용 모든 설교 불러오그 (private & public) ")
    @GetMapping("/admin/list")
    public List<SermonResponseDTO> findAllWithOwner() {
        return sermonService.getAllSermons();
    }

    @Operation(summary = "유저별 sermons list all, private, public! ( UserSermonList ) ")
    @GetMapping("/user/list")
    public List<SermonResponseDTO> getUserSermons(
            @RequestParam("userId") String userId,
            @RequestParam("option") String option) {
        switch (option.toLowerCase()) {
            case "all":
                return sermonService.getAllSermonsByUser(userId);
            case "private":
                return sermonService.getPrivateSermons(userId);
            case "public":
                return sermonService.getPublicSermonsByUser(userId);
            default:
                throw new IllegalArgumentException("Invalid option. Valid options are: all, private, public.");
        }
    }


    // Get details of a specific sermon
    @Operation(summary = " 선택한 설교 details 페이지 contents 도 보내드림 ( SermonDetails )")
    @GetMapping("/details/{sermonId}")
    public SermonResponseDTO getSermonDetails(@PathVariable Long sermonId) {
        return sermonService.getSermonDetails(sermonId); // <--- UUID 추가
    }

    // CREATE new sermon
    @Operation(summary = "설교 추가 ( AddSermon ) ")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully created new sermon"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public SermonResponseDTO createSermon(@RequestBody SermonRequestDTO requestDTO) {
        return sermonService.createSermon(requestDTO);
    }

    // PATCH sermon
    @Operation(summary = "설교 및 내용 업데이트 (Update Sermon and Content)", description = "특정 설교와 그 내용을 동시에 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated the sermon"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Sermon not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized to update this sermon"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/update/{sermonId}")
    public SermonResponseDTO updateSermonAndContent(@PathVariable Long sermonId,
                                                    @RequestParam("userId") String loggedInUserId,
                                                    @RequestBody SermonRequestDTO requestDTO) {
        return sermonService.updateSermonAndContent(sermonId, loggedInUserId, requestDTO);
    }



    // DELETE sermon
    @Operation(summary = "설교 삭제. 낄낄 PATCH 과 같이 로그인된 유저 아디도 보내주셈( DeleteSermon ) ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully deleted the sermon"),
            @ApiResponse(responseCode = "404", description = "Sermon not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized to delete this sermon"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{sermonId}")
    public void deleteSermon(@PathVariable Long sermonId,
                             @RequestParam("userId") String loggedInUserId) {
        sermonService.deleteSermon(sermonId, loggedInUserId);
    }

    @Operation(summary = "설교 SEARCH ! 케이스 3개로 나눴어요 ~ ( SearchSermon ) ", description = " searchin = 에서 title == keyword이 제목에만 있는 설교 불러오기, content == 본문에 keyword가 있는 설교들 불러오기, both == 제목 본문 둘다~ ")
    @GetMapping("/search")
    public List<SermonResponseDTO> searchSermons(@RequestParam("keyword") String keyword,
                                                 @RequestParam("userId") String userId,
                                                 @RequestParam(value = "searchIn", defaultValue = "both") String searchIn) {
        return sermonService.searchSermons(keyword, userId, searchIn);
    }

    @Operation(summary = "필터링된 설교 목록 가져오기", description = "정렬, 예배 유형, 작성자(이름으로), 날짜 범위를 기준으로 필터링하여 설교 목록을 반환")
    @GetMapping("/filtered-list")
    public List<SermonResponseDTO> getFilteredSermons(
            @RequestParam(value = "sort", defaultValue = "desc") String sortOrder,
            @RequestParam(value = "worshipType", defaultValue = "all") String worshipType,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        return sermonService.getFilteredSermons(sortOrder, worshipType, author, startDate, endDate);
    }

}
