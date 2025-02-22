package com.project.bibly_be.text.controller;

import com.project.bibly_be.text.dto.TextContentRequest;
import com.project.bibly_be.text.dto.TextResponse;
import com.project.bibly_be.text.dto.TextSummary;
import com.project.bibly_be.text.service.TextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/text")
public class TextController {

    private final TextService textService;

    @Autowired
    public TextController(TextService textService) {
        this.textService = textService;
    }

    @Operation(summary = "Add a new text",
            description = "Creates a new text entry for a sermon. Accepts sermonId, userId, isDraft flag, and textTitle as request parameters, with textContent provided in the JSON body.")
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

    @Operation(summary = "Get text summaries for a sermon",
            description = "Retrieves texts for a given sermon, returning only summary information (without textContent).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved text summaries"),
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

    @Operation(summary = "Get text details",
            description = "Retrieves full text details for a given sermon and text ID, including textContent.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved text details"),
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

    @Operation(summary = "Update a text",
            description = "Partially updates a text entry if the requesting user is the text owner or an admin. Accepts textTitle and isDraft as request parameters, with textContent provided in the JSON body.")
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

    @Operation(summary = "Delete a text",
            description = "Deletes a text entry if the requesting user is the text owner or an admin.")
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
