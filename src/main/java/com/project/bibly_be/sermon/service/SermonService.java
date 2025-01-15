package com.project.bibly_be.sermon.service;

import com.project.bibly_be.sermon.dto.ContentDTO;
import com.project.bibly_be.sermon.dto.SermonRequestDTO;
import com.project.bibly_be.sermon.dto.SermonResponseDTO;
import com.project.bibly_be.sermon.entity.Content;
import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.sermon.repository.ContentRepository;
import com.project.bibly_be.sermon.repository.SermonRepository;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SermonService {
    private final SermonRepository sermonRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    public SermonResponseDTO createSermon(SermonRequestDTO requestDTO) {
        // Fetch user
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Parse sermonDate
        LocalDate sermonDate = LocalDate.parse(requestDTO.getSermonDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Generate unique file code
        String fileCode = generateUniqueFileCode(sermonDate);

        // Create and save the Sermon entity
        Sermon sermon = Sermon.builder()
                .owner(user)
                .sermonDate(sermonDate.atStartOfDay())
                .isPublic(requestDTO.isPublic())
                .worshipType(requestDTO.getWorshipType())
                .mainScripture(requestDTO.getMainScripture())
                .additionalScripture(requestDTO.getAdditionalScripture())
                .sermonTitle(requestDTO.getSermonTitle())
                .summary(requestDTO.getSummary())
                .notes(requestDTO.getNotes())
                .recordInfo(requestDTO.getRecordInfo())
                .fileCode(fileCode)
                .build();

        Sermon savedSermon = sermonRepository.save(sermon);

        // Create and save the Content entity
        Content content = Content.builder()
                .sermon(savedSermon)
                .fileCode(fileCode)
                .contentText(requestDTO.getContentText())
                .build();

        contentRepository.save(content);

        // Return response
        return SermonResponseDTO.builder()
                .sermonId(savedSermon.getSermonId())
                .ownerName(savedSermon.getOwner().getName())
                .sermonDate(savedSermon.getSermonDate())
                .createdAt(savedSermon.getCreatedAt())
                .updatedAt(savedSermon.getUpdatedAt())
                .isPublic(savedSermon.isPublic())
                .worshipType(savedSermon.getWorshipType())
                .mainScripture(savedSermon.getMainScripture())
                .additionalScripture(savedSermon.getAdditionalScripture())
                .sermonTitle(savedSermon.getSermonTitle())
                .summary(savedSermon.getSummary())
                .notes(savedSermon.getNotes())
                .recordInfo(savedSermon.getRecordInfo())
                .fileCode(savedSermon.getFileCode())
                .build();
    }

    private String generateUniqueFileCode(LocalDate sermonDate) {
        String baseFileCode = sermonDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileCode = baseFileCode;
        int counter = 1;

        // Check for existing fileCode in the database
        while (sermonRepository.existsByFileCode(fileCode)) {
            fileCode = baseFileCode + "_" + counter;
            counter++;
        }

        return fileCode;
    }


    // Get all public sermons
    public List<SermonResponseDTO> getAllPublicSermons() {
        return sermonRepository.findByIsPublicTrue().stream()
                .map(sermon -> SermonResponseDTO.builder()
                        .sermonId(sermon.getSermonId())
                        .ownerName(sermon.getOwner().getName())
                        .sermonDate(sermon.getSermonDate())
                        .createdAt(sermon.getCreatedAt())
                        .updatedAt(sermon.getUpdatedAt())
                        .isPublic(sermon.isPublic())
                        .worshipType(sermon.getWorshipType())
                        .mainScripture(sermon.getMainScripture())
                        .additionalScripture(sermon.getAdditionalScripture())
                        .sermonTitle(sermon.getSermonTitle())
                        .summary(sermon.getSummary())
                        .notes(sermon.getNotes())
                        .recordInfo(sermon.getRecordInfo())
                        .fileCode(sermon.getFileCode())
                        .build())
                .collect(Collectors.toList());
    }

    // Update a sermon
    public SermonResponseDTO updateSermon(Long sermonId, SermonRequestDTO requestDTO, String loggedInUserId) {
        // Fetch the sermon to update
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new IllegalArgumentException("Sermon not found"));

        // Check ownership
        if (!sermon.getOwner().getId().toString().equals(loggedInUserId)) {
            throw new IllegalArgumentException("Unauthorized to update this sermon");
        }

        // Update fields
        sermon.setSermonDate(LocalDate.parse(requestDTO.getSermonDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay());
        sermon.setWorshipType(requestDTO.getWorshipType());
        sermon.setMainScripture(requestDTO.getMainScripture());
        sermon.setAdditionalScripture(requestDTO.getAdditionalScripture());
        sermon.setSermonTitle(requestDTO.getSermonTitle());
        sermon.setSummary(requestDTO.getSummary());
        sermon.setNotes(requestDTO.getNotes());
        sermon.setRecordInfo(requestDTO.getRecordInfo());
        sermon.setPublic(requestDTO.isPublic());

        // Save the updated sermon
        Sermon updatedSermon = sermonRepository.save(sermon);

        // Build and return the response
        return SermonResponseDTO.builder()
                .sermonId(updatedSermon.getSermonId())
                .ownerName(updatedSermon.getOwner().getName())
                .sermonDate(updatedSermon.getSermonDate())
                .createdAt(updatedSermon.getCreatedAt())
                .updatedAt(updatedSermon.getUpdatedAt())
                .isPublic(updatedSermon.isPublic())
                .worshipType(updatedSermon.getWorshipType())
                .mainScripture(updatedSermon.getMainScripture())
                .additionalScripture(updatedSermon.getAdditionalScripture())
                .sermonTitle(updatedSermon.getSermonTitle())
                .summary(updatedSermon.getSummary())
                .notes(updatedSermon.getNotes())
                .recordInfo(updatedSermon.getRecordInfo())
                .fileCode(updatedSermon.getFileCode())
                .build();
    }


    // Delete a sermon
    public void deleteSermon(Long sermonId, String loggedInUserId) {
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new IllegalArgumentException("Sermon not found"));

        if (!sermon.getOwner().getId().toString().equals(loggedInUserId)) {
            throw new IllegalArgumentException("Unauthorized to delete this sermon");
        }

        sermonRepository.delete(sermon);
    }

}