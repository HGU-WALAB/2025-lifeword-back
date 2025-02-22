package com.project.bibly_be.text.controller;

import com.project.bibly_be.text.dto.TextContentRequest;
import com.project.bibly_be.text.dto.TextPatchRequest;
import com.project.bibly_be.text.dto.TextResponseDTO;
import com.project.bibly_be.text.service.TextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/texts")
public class TextController {

    private final TextService textService;

    @Autowired
    public TextController(TextService textService) {
        this.textService = textService;
    }

    @Operation(summary = "Add a new text",
            description = "Creates a new text entry for a sermon. Accepts sermonId, userId, isDraft flag, textTitle as request parameters and textContent in the JSON body.")
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

    @Operation(summary = "Get texts for a sermon",
            description = "Retrieves texts for a given sermon. Returns public texts and, for drafts, only if the requesting user matches the text owner or is an admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved texts"),
            @ApiResponse(responseCode = "404", description = "Sermon or texts not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{sermonId}")
    public ResponseEntity<List<TextResponseDTO>> getTexts(
            @PathVariable("sermonId") Long sermonId,
            @RequestParam("userId") String userId) {

        List<TextResponseDTO> dtos = textService.getTextsForSermon(sermonId, userId);
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Update a text",
            description = "Partially updates a text entry if the requesting user is the text owner or an admin.")
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
            @RequestBody TextPatchRequest request) {

        textService.patchText(textId, userId, request);
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
