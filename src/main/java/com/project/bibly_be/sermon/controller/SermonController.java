package com.project.bibly_be.sermon.controller;

import com.project.bibly_be.sermon.dto.ContentDTO;
import com.project.bibly_be.sermon.dto.SermonRequestDTO;
import com.project.bibly_be.sermon.dto.SermonResponseDTO;
import com.project.bibly_be.sermon.service.SermonService;
import com.project.bibly_be.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sermon")
@RequiredArgsConstructor
public class SermonController {

    private final SermonService sermonService;
    private final UserRepository userRepository;



    // GET all public sermons
    @Operation(summary = "public 되어있는 설교 다 불러오기 룰루 ( SermonPublicList) ")
    @GetMapping("/publiclist")
    public List<SermonResponseDTO> getAllPublicSermons() {
        return sermonService.getAllPublicSermons();
    }

    // GET private sermons of the logged-in user
    @Operation(summary = " 로그인된 유저 private 되어있는 설교 다 불러오기 룰루 ( SermonPrivateList )", description = " 유저아이디 보내주면 리스트 불러드를게여~")
    @GetMapping("/private")
    public List<SermonResponseDTO> getPrivateSermons(@RequestParam("userId") String userId) {
        return sermonService.getPrivateSermons(userId);
    }
    // Get details of a specific sermon
    @Operation(summary = " 선택한 설교 details 페이지 contents 도 보내드림 ( SermonDetails )")
    @GetMapping("/{sermonId}/details")
    public SermonResponseDTO getSermonDetails(@PathVariable Long sermonId) {
        return sermonService.getSermonDetails(sermonId);
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


    @Operation(summary = "설교 ㅋㅋㅋ 한번에 많이 추가하는거 만듬.. 그냥 .. 하나씩 추가하기 귀찮아서요 >< 히히힣ㅎㅎ ")
    @PostMapping("/bulk")
    public ResponseEntity<?> createSermons(@RequestBody List<SermonRequestDTO> sermonRequestDTOList) {
        List<SermonResponseDTO> responseList = sermonService.createSermons(sermonRequestDTOList);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseList);
    }


    // PATCH sermon
    @Operation(summary = "설교 수정띠 , 로그인된  ID 보내 주시면 비교해서 업뎃해줌( UpdateSermon ) ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated the sermon"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Sermon not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized to update this sermon"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{sermonId}")
    public SermonResponseDTO updateSermon(@PathVariable Long sermonId,
                                          @RequestBody SermonRequestDTO requestDTO,
                                          @RequestParam("userId") String loggedInUserId) {
        return sermonService.updateSermon(sermonId, requestDTO, loggedInUserId);
    }

    @Operation(summary = "설교 본문 수정띠 ( UpdateContent ) ", description = "혅웅 오빠 이거 업뎃 할때 quotation marks 까지 저장된다면 말해주세용 그거 고쳐줘야흠")
    @PatchMapping("/{sermonId}/content")
    public ContentDTO updateContent(@PathVariable Long sermonId,
                                    @RequestParam("userId") String loggedInUserId,
                                    @RequestBody String contentText) {
        return sermonService.updateContent(sermonId, contentText, loggedInUserId);
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

}
