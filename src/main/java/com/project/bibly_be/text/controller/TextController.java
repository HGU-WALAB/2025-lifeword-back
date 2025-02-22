package com.project.bibly_be.text.controller;

import com.project.bibly_be.text.dto.TextContentRequest;
import com.project.bibly_be.text.dto.TextResponse;
import com.project.bibly_be.text.dto.TextSummary;
import com.project.bibly_be.text.service.TextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/text")
@Tag(name = " 🙋‍♀️ 혅웅햄 여기여 Text - controller ", description = "구 contents.. ㅋㅋ 관리 편하게 아예 따로 빼서 작업함 ~ ")
public class TextController {

        private final TextService textService;

        @Autowired
        public TextController(TextService textService) {
                this.textService = textService;
        }

        @Operation(summary = "설교에 text 추가하기 (AddText) ", description = "선택한 sermon 에 text 를 추가한다.\n Request Param으로 sermonId, userId, isDraft (true == 임시저장 느낌인거고 / 비공개, false == 공개 ), and textTitle 를 받고, JSON body로 textContent를 서버로 넘겨준다면 text 하나를 생성한다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Text added successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PostMapping("/create")
        public ResponseEntity<String> createText(
                        @RequestParam("sermonId") Long sermonId,
                        @RequestParam("userId") String userId,
                        @RequestParam("isDraft") boolean isDraft,
                        @RequestParam("textTitle") String textTitle,
                        @RequestBody TextContentRequest textContentRequest) {

                textService.createText(sermonId, userId, isDraft, textTitle, textContentRequest.getTextContent());
                return ResponseEntity.ok("Added successfully");
        }

        @Operation(summary = "선택한 text 수정하기 (Update Text)", description = "POST 랑 같은 형태 유지!! 했는데 오빠 작업하기 편한 방법 있으면 알려줘유 바꿔드림 \n userId 를 비교헤서 편집 권한 있는지 없는지 확인함 단! admin 은 userId 달라도 편집 허용.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Successfully updated the text"),
                @ApiResponse(responseCode = "400", description = "Invalid request payload"),
                @ApiResponse(responseCode = "403", description = "Unauthorized to update this text"),
                @ApiResponse(responseCode = "404", description = "Text not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PatchMapping("/update/{textId}")
        public ResponseEntity<String> patchText(
                @PathVariable("textId") Long textId,
                @RequestParam("userId") String userId,
                @RequestParam("textTitle") String textTitle,
                @RequestParam("isDraft") boolean isDraft,
                @RequestBody TextContentRequest textContentRequest) {

                textService.patchText(textId, userId, textTitle, isDraft, textContentRequest.getTextContent());
                return ResponseEntity.ok("Updated successfully");
        }

        @Operation(summary = "Get list of texts in sermon", description = "한 설교에 있는 모든 text 를 lsit 으로 보냄. textContent 외에 모든 정보~~ \n /{sermonId}/{textId} 가 text의 details ( textcontent) 까지 보내는 endpoint 임  ")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
                        @ApiResponse(responseCode = "404", description = "Sermon or texts not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/list/{sermonId}")
        public ResponseEntity<List<TextSummary>> getTextSummaries(
                        @PathVariable("sermonId") Long sermonId,
                        @RequestParam("userId") String userId) {

                List<TextSummary> summaries = textService.getTextSummariesForSermon(sermonId, userId);
                return ResponseEntity.ok(summaries);
        }

        @Operation(summary = "선택한 text 의 contents 전체 불러오기 (Text Details)", description = "선택한 text 의 textContent 까지 불러옴 ( details 페이지라고 생각하면 편함 )")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
                        @ApiResponse(responseCode = "404", description = "Text not found"),
                        @ApiResponse(responseCode = "403", description = "Unauthorized access"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/{sermonId}/{textId}")
        public ResponseEntity<TextResponse> getTextDetails(
                        @PathVariable("sermonId") Long sermonId,
                        @PathVariable("textId") Long textId,
                        @RequestParam("userId") String userId) {

                TextResponse dto = textService.getTextDetail(sermonId, textId, userId);
                return ResponseEntity.ok(dto);
        }



        @Operation(summary = "선택한 text 삭제하기 (Delete Text)", description = "text 삭제 ! userId 를 비교헤서 삭제 권한 있는지 없는지 확인함 단! admin 은 userId 달라도 삭제 허용.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully deleted the text"),
                        @ApiResponse(responseCode = "403", description = "Unauthorized to delete this text"),
                        @ApiResponse(responseCode = "404", description = "Text not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @DeleteMapping("/delete/{textId}")
        public ResponseEntity<String> deleteText(
                        @PathVariable("textId") Long textId,
                        @RequestParam("userId") String userId) {

                textService.deleteText(textId, userId);
                return ResponseEntity.ok("Deleted successfully");
        }
}
