package com.project.bibly_be.sermon.controller;

import com.project.bibly_be.sermon.dto.SermonRequestDTO;
import com.project.bibly_be.sermon.dto.SermonResponseDTO;
import com.project.bibly_be.sermon.dto.SermonResponsePageDTO;
import com.project.bibly_be.sermon.service.SermonService;
import com.project.bibly_be.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sermons")
@RequiredArgsConstructor
public class SermonController {

    private final SermonService sermonService;
    private final UserRepository userRepository;

    // GET all public sermons
    // @Operation(summary = "public 되어있는 설교 다 불러오기 룰루 ( SermonPublicList ) ")
    // @GetMapping("/publiclist")
    // public List<SermonResponseDTO> getAllPublicSermons() {
    // return sermonService.getAllPublicSermons();
    // }
    //
    @Operation(summary = "Admin 용 모든 설교 불러오그 (private & public) ")
    @GetMapping("/admin/list")
    public List<SermonResponseDTO> findAllWithOwner() {
        return sermonService.getAllSermons();
    }

    // @Operation(summary = "유저별 sermons list all, private, public! ( UserSermonList
    // ) ")
    // @GetMapping("/user/list")
    // public List<SermonResponseDTO> getUserSermons(
    // @RequestParam("userId") String userId,
    // @RequestParam("option") String option) {
    // switch (option.toLowerCase()) {
    // case "all":
    // return sermonService.getAllSermonsByUser(userId);
    // case "private":
    // return sermonService.getPrivateSermons(userId);
    // case "public":
    // return sermonService.getPublicSermonsByUser(userId);
    // default:
    // throw new IllegalArgumentException("Invalid option. Valid options are: all,
    // private, public.");
    // }
    // }

    // Get details of a specific sermon
    @Operation(summary = " 선택한 설교 details ( SermonDetails )")
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
    public SermonResponseDTO updateSermon(@PathVariable Long sermonId,
            @RequestParam("userId") String loggedInUserId,
            @RequestBody SermonRequestDTO requestDTO) {
        return sermonService.updateSermon(sermonId, loggedInUserId, requestDTO);
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

    @Operation(summary = "설교 목록 검색 (자동 우선순위 적용)", description = "1. 작성자 검색 → 2. 제목 검색 → 3. 본문 검색")
    @GetMapping("/search")
    public List<SermonResponseDTO> searchSermons(@RequestParam("keyword") String keyword) {
        return sermonService.searchSermons(keyword);
    }

    // HIDE ( 삭제대신 숨김기능)
    @Operation(summary = "삭제 대신 '숨김' 기능으로 사용", description = "프론트에서 보내주는 sermon Id 를 리스트로 받아서 뒤에서 알어서 처리해드림~")
    @PatchMapping("/batch/hide")
    public ResponseEntity<Void> hideSermons(@RequestBody List<Long> sermonIds) {
        sermonService.hideSermons(sermonIds);
        return ResponseEntity.ok().build();
    }


    // @Operation(summary = "필터링된 설교 목록 가져오기", description = "정렬, 예배 유형, 작성자(이름으로),
    // 날짜 범위를 기준으로 필터링하여 설교 목록을 반환")
    // @GetMapping("/filtered-list")
    // public List<SermonResponseDTO> getFilteredSermons(
    // @RequestParam(value = "sort", defaultValue = "desc") String sortOrder,
    // @RequestParam(value = "worshipType", defaultValue = "all") String
    // worshipType,
    // @RequestParam(value = "startDate", required = false) String startDate,
    // @RequestParam(value = "endDate", required = false) String endDate,
    // @RequestParam(value = "scripture", required = false) String scripture
    // ) {
    // return sermonService.getFilteredSermons(sortOrder, worshipType, startDate,
    // endDate,scripture);
    // }

    @Operation(summary = "필터링 설교 목록 User Page", description = "page 는 1 부터 시작 , keyword (null 은 모든 검색) 1. 작성자 검색 → 2. 제목 검색 → 3. 본문 검색 \n"
            +
            "//유저아이디 (UUID) 필수 , 정렬( desc = 최신순 (default), asc =  오래된순 , recent =  최근 수정), 예배 유형(default = null (empty)),\n"
            +
            " page 페이지 오프셋 , size 한 페이지에 들어갈 설교 수, 모드 (0 = 공개 설교 (default) , 1 = 내 전체 설교, 2 = 내 공개 설교, 3 = 내 비공개 설교) \n"
            +
            "// page = 1, size = 10 (default)       날짜 범위를 기준으로 필터링하여 설교 목록을 반환.totalPage = 총 페이지수 반환")
    @GetMapping("/filtered-list-user")
    public SermonResponsePageDTO getFilteredSermonsPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "user_id", required = true) UUID userId,
            @RequestParam(value = "sort", defaultValue = "desc") String sortOrder,
            @RequestParam(value = "worshipType", required = false) List<String> worshipType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "scripture", required = false) List<String> scripture,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "mode", defaultValue = "0") int mode) {
        return sermonService.searchSermonFilterUser(keyword, userId, sortOrder, worshipType, startDate, endDate,
                scripture, page, size, mode);
    }

    @Operation(summary = "필터링 설교 목록 관리자용 Page", description = "page 는 1 부터 시작 " +
            "  keyword (null 은 모든 검색) " +
            "//정렬( desc = 최신순 , asc =  오래된순(default) , recent =  최근 수정) \n" +
            " 예배 유형( all (default)),\n" +
            "page 페이지 오프셋 , size 한 페이지에 들어갈 설교 수\n" +
            " page = 1 , size = 10   날짜 범위를 기준으로 필터링하여 설교 목록을 반환. totalPage = 총 페이지수 반환")
    @GetMapping("/filtered-list-admin")
    public SermonResponsePageDTO getFilteredSermonsPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", defaultValue = "desc") String sortOrder,
            @RequestParam(value = "worshipType", required = false) List<String> worshipType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "scripture", required = false) List<String> scripture,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return sermonService.searchSermonsFiltered(keyword, sortOrder, worshipType, startDate, endDate, scripture, page,
                size);
    }

}
