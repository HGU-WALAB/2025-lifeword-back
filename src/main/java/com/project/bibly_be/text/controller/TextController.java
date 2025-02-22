package com.project.bibly_be.text.controller;

import com.project.bibly_be.text.dto.TextContentRequest;
import com.project.bibly_be.text.service.TextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/texts")
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
        textService.createText(sermonId, userId, isDraft, textTitle, textContentRequest.getTextContent());
        return ResponseEntity.ok("성공적으로 추가하였습니다");
    }
}
