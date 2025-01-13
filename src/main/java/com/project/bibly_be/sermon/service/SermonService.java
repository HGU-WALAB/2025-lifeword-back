package com.project.bibly_be.sermon.service;

import com.project.bibly_be.sermon.dto.SermonRequestDto;
import com.project.bibly_be.sermon.dto.SermonResponseDto;
import com.project.bibly_be.sermon.entity.Content;
import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.sermon.repo.ContentRepo;
import com.project.bibly_be.sermon.repo.SermonRepo;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SermonService {

    private final SermonRepo sermonRepo;
    private final UserRepository userRepository;

    public SermonService(UserRepository userRepository, SermonRepo sermonRepo) {
        this.userRepository = userRepository;
        this.sermonRepo = sermonRepo;
    }

    public SermonResponseDto createSermon(SermonRequestDto requestDto, UUID loggedInUserUuid) {
        // Fetch user by UUID
        User user = userRepository.findById(loggedInUserUuid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Populate sermon with user information
        Sermon sermon = new Sermon();
        sermon.setSermonOwnerId(user.getId());
        sermon.setSermonOwner(user.getName());
        sermon.setWorshipType(requestDto.getWorshipType());
        sermon.setMainScripture(requestDto.getMainScripture());
        sermon.setAdditionalScripture(requestDto.getAdditionalScripture());
        sermon.setSermonTitle(requestDto.getSermonTitle());
        sermon.setSummary(requestDto.getSummary());
        sermon.setNotes(requestDto.getNotes());
        sermon.setRecordInfo(requestDto.getRecordInfo());
        sermon.setFileCode(requestDto.getFileCode());
        sermon.setIsPublic(requestDto.getIsPublic());
        sermon.setSermonUpdatedAt(LocalDateTime.now());

        // Add content
        Content content = new Content();
        content.setContentText(requestDto.getContentText());
        content.setSermon(sermon);

        sermon.setContent(content);

        sermonRepo.save(sermon);
        return mapToResponseDto(sermon);
    }


    public SermonResponseDto updateSermon(Long sermonId, SermonRequestDto requestDto, String loggedInUserId) {
        Sermon sermon = sermonRepo.findById(sermonId)
                .orElseThrow(() -> new RuntimeException("Sermon not found"));

        if (!sermon.getSermonOwnerId().equals(loggedInUserId)) {
            throw new RuntimeException("Unauthorized to update this sermon");
        }

        updateSermonFields(sermon, requestDto);

        Content content = sermon.getContent();
        if (content != null && requestDto.getContentText() != null &&
                !requestDto.getContentText().equals(content.getContentText())) {
            content.setContentText(requestDto.getContentText());
            content.setContentUpdatedAt(LocalDateTime.now());
        }

        sermon.setSermonUpdatedAt(LocalDateTime.now());
        sermonRepo.save(sermon);

        return mapToResponseDto(sermon);
    }

    private void updateSermonFields(Sermon sermon, SermonRequestDto requestDto) {
        if (requestDto.getSermonTitle() != null) {
            sermon.setSermonTitle(requestDto.getSermonTitle());
        }
        if (requestDto.getSummary() != null) {
            sermon.setSummary(requestDto.getSummary());
        }
        if (requestDto.getWorshipType() != null) {
            sermon.setWorshipType(requestDto.getWorshipType());
        }
        if (requestDto.getMainScripture() != null) {
            sermon.setMainScripture(requestDto.getMainScripture());
        }
        if (requestDto.getAdditionalScripture() != null) {
            sermon.setAdditionalScripture(requestDto.getAdditionalScripture());
        }
        if (requestDto.getRecordInfo() != null) {
            sermon.setRecordInfo(requestDto.getRecordInfo());
        }
        if (requestDto.getFileCode() != null) {
            sermon.setFileCode(requestDto.getFileCode());
        }
        if (requestDto.getIsPublic() != null) {
            sermon.setIsPublic(requestDto.getIsPublic());
        }

    }

    public SermonResponseDto getSermonById(Long sermonId) {
        Sermon sermon = sermonRepo.findById(sermonId)
                .orElseThrow(() -> new RuntimeException("Sermon not found"));
        return mapToResponseDto(sermon);
    }

    public void deleteSermon(Long sermonId, String loggedInUserId) {
        Sermon sermon = sermonRepo.findById(sermonId)
                .orElseThrow(() -> new RuntimeException("Sermon not found"));

        if (!sermon.getSermonOwnerId().equals(loggedInUserId)) {
            throw new RuntimeException("Unauthorized to delete this sermon");
        }

        sermonRepo.delete(sermon);
    }

    public List<SermonResponseDto> getAllSermons(Boolean isPublic) {
        List<Sermon> sermons;
        if (isPublic != null) {
            sermons = sermonRepo.findByIsPublic(isPublic);
        } else {
            sermons = sermonRepo.findAll();
        }

        return sermons.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private SermonResponseDto mapToResponseDto(Sermon sermon) {
        SermonResponseDto dto = new SermonResponseDto();
        dto.setSermonId(sermon.getSermonId());
        dto.setSermonOwnerId(sermon.getSermonOwnerId());
        dto.setSermonOwner(sermon.getSermonOwner());
        dto.setIsPublic(sermon.getIsPublic());
        dto.setWorshipType(sermon.getWorshipType());
        dto.setMainScripture(sermon.getMainScripture());
        dto.setAdditionalScripture(sermon.getAdditionalScripture());
        dto.setSermonTitle(sermon.getSermonTitle());
        dto.setSummary(sermon.getSummary());
        dto.setNotes(sermon.getNotes());
        dto.setRecordInfo(sermon.getRecordInfo());
        dto.setFileCode(sermon.getFileCode());
        dto.setSermonCreatedAt(sermon.getSermonCreatedAt());
        dto.setSermonUpdatedAt(sermon.getSermonUpdatedAt());
        if (sermon.getContent() != null) {
            dto.setContentText(sermon.getContent().getContentText());
        }
        return dto;
    }
}
