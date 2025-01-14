package com.project.bibly_be.sermon.controller;

import com.project.bibly_be.sermon.dto.SermonRequestDto;
import com.project.bibly_be.sermon.dto.SermonResponseDto;
import com.project.bibly_be.sermon.service.SermonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/sermons")
public class SermonController {

    private final SermonService sermonService;

    public SermonController(SermonService sermonService) {
        this.sermonService = sermonService;
    }

    // Create a new sermon
    @PostMapping("/sermons")
    public ResponseEntity<SermonResponseDto> createSermon(
            @RequestBody SermonRequestDto sermonRequestDto,
            @RequestHeader("loggedInUserUuid") String loggedInUserUuid) {
        UUID userUuid = UUID.fromString(loggedInUserUuid);
        SermonResponseDto response = sermonService.createSermon(sermonRequestDto, userUuid);
        return ResponseEntity.ok(response);
    }



    // Get all sermons (optionally filtered by public status)
    @GetMapping
    public ResponseEntity<List<SermonResponseDto>> getAllSermons(@RequestParam(required = false) Boolean isPublic) {
        // Assume a method in the service layer to fetch sermons based on the public flag
        List<SermonResponseDto> sermons = sermonService.getAllSermons(isPublic);
        return ResponseEntity.ok(sermons);
    }

    // Get a sermon by ID
    @GetMapping("/{sermonId}")
    public ResponseEntity<SermonResponseDto> getSermonById(@PathVariable Long sermonId) {
        SermonResponseDto response = sermonService.getSermonById(sermonId);
        return ResponseEntity.ok(response);
    }

    // Update a sermon
    @PatchMapping("/{sermonId}")
    public ResponseEntity<SermonResponseDto> patchSermon(
            @PathVariable Long sermonId,
            @RequestBody SermonRequestDto sermonRequestDto,
            @RequestHeader("loggedInUserId") String loggedInUserId) {
        SermonResponseDto response = sermonService.updateSermon(sermonId, sermonRequestDto, loggedInUserId);
        return ResponseEntity.ok(response);
    }


    // Delete a sermon
    @DeleteMapping("/{sermonId}")
    public ResponseEntity<Void> deleteSermon(
            @PathVariable Long sermonId,
            @RequestHeader("loggedInUserId") String loggedInUserId
    ) {
        sermonService.deleteSermon(sermonId, loggedInUserId);
        return ResponseEntity.noContent().build();
    }
}