package com.project.bibly_be.text.controller;

import com.project.bibly_be.text.dto.TextContentRequest;
import com.project.bibly_be.text.dto.TextPatchRequest;
import com.project.bibly_be.text.dto.TextResponseDTO;
import com.project.bibly_be.text.service.TextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/texts")
public class TextController {

    private final TextService textService;

    @Autowired
    public TextController(TextService textService) {
        this.textService = textService;
    }

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

    @GetMapping("/list/{sermonId}")
    public ResponseEntity<List<TextResponseDTO>> getTexts(
            @PathVariable("sermonId") Long sermonId,
            @RequestParam("userId") String userId) {

        List<TextResponseDTO> dtos = textService.getTextsForSermon(sermonId, userId);
        return ResponseEntity.ok(dtos);
    }
    @PatchMapping("/update/{textId}")
    public ResponseEntity<String> patchText(
            @PathVariable("textId") Long textId,
            @RequestParam("userId") String userId,
            @RequestBody TextPatchRequest request) {

        textService.patchText(textId, userId, request);
        return ResponseEntity.ok("Updated successfully");
    }

    @DeleteMapping("/delete/{textId}")
    public ResponseEntity<String> deleteText(
            @PathVariable("textId") Long textId,
            @RequestParam("userId") String userId) {

        textService.deleteText(textId, userId);
        return ResponseEntity.ok("Deleted successfully");
    }

}
