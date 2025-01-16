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
import java.time.format.DateTimeFormatter;
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
    
    //귀찮은 나를 위해 만든거
    public List<SermonResponseDTO> createSermons(List<SermonRequestDTO> requestDTOList) {
        return requestDTOList.stream()
                .map(this::createSermon)
                .collect(Collectors.toList());
    }


    public SermonResponseDTO createSermon(SermonRequestDTO requestDTO) {
        // FETCH user
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // PARSE
        LocalDate sermonDate = LocalDate.parse(requestDTO.getSermonDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // GENERATE unique file code
        String fileCode = generateUniqueFileCode(sermonDate);

        // CREATE and SAVE the Sermon
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

        // CREATE and SAVE the Content
        Content content = Content.builder()
                .sermon(savedSermon)
                .fileCode(fileCode)
                .contentText(requestDTO.getContentText())
                .build();

        contentRepository.save(content);
        
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

        // if filecode exists _counter 추가해서 저장
        while (sermonRepository.existsByFileCode(fileCode)) {
            fileCode = baseFileCode + "_" + counter;
            counter++;
        }

        return fileCode;
    }


    // GET all public sermons
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

    // GET private sermons for logged user
    public List<SermonResponseDTO> getPrivateSermons(String userId) {
        UUID userUUID = UUID.fromString(userId); // convert string > UUID
        return sermonRepository.findByOwner_IdAndIsPublicFalse(userUUID).stream()
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

    // GET details of sermon by ID
    public SermonResponseDTO getSermonDetails(Long sermonId) {
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new IllegalArgumentException("Sermon not found"));

        List<ContentDTO> contents = sermon.getContents().stream()
                .map(content -> ContentDTO.builder()
                        .contentId(content.getContentId())
                        .fileCode(content.getFileCode())
                        .contentText(content.getContentText())
                        .build())
                .collect(Collectors.toList());

        return SermonResponseDTO.builder()
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
                .contents(contents)
                .build();
    }

    // PATCH a sermon
    public SermonResponseDTO updateSermon(Long sermonId, SermonRequestDTO requestDTO, String loggedInUserId) {
        UUID userUUID = UUID.fromString(loggedInUserId);

        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new IllegalArgumentException("Sermon not found"));

        // 로그인된 유저가 주인장 아니면....
        if (!sermon.getOwner().getId().equals(userUUID)) {
            throw new IllegalArgumentException("Unauthorized: Logged-in user is not the owner of this sermon.");
        }
        sermon.setSermonDate(LocalDate.parse(requestDTO.getSermonDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay());
        sermon.setWorshipType(requestDTO.getWorshipType());
        sermon.setMainScripture(requestDTO.getMainScripture());
        sermon.setAdditionalScripture(requestDTO.getAdditionalScripture());
        sermon.setSermonTitle(requestDTO.getSermonTitle());
        sermon.setSummary(requestDTO.getSummary());
        sermon.setNotes(requestDTO.getNotes());
        sermon.setRecordInfo(requestDTO.getRecordInfo());
        sermon.setPublic(requestDTO.isPublic());

        Sermon updatedSermon = sermonRepository.save(sermon);

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

    public ContentDTO updateContent(Long sermonId, String contentText, String loggedInUserId) {
        UUID userUUID = UUID.fromString(loggedInUserId);

        // Fetch the sermon
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new IllegalArgumentException("Sermon not found"));

        // Check ownership
        if (!sermon.getOwner().getId().equals(userUUID)) {
            throw new IllegalArgumentException("Unauthorized: Logged-in user is not the owner of this sermon.");
        }

        // Fetch or create content
        Content content = sermon.getContents().isEmpty() ? null : sermon.getContents().get(0);
        if (content == null) {
            content = new Content();
            content.setSermon(sermon);
            content.setFileCode(sermon.getFileCode()); // Assign sermon fileCode
        }

        // Update content text
        content.setContentText(contentText);
        Content updatedContent = contentRepository.save(content);

        return ContentDTO.builder()
                .contentId(updatedContent.getContentId())
                .fileCode(updatedContent.getFileCode())
                .contentText(updatedContent.getContentText())
                .build();
    }





    // DELETE sermon
    public void deleteSermon(Long sermonId, String loggedInUserId) {
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new IllegalArgumentException("Sermon not found"));

        if (!sermon.getOwner().getId().toString().equals(loggedInUserId)) {
            throw new IllegalArgumentException("Unauthorized to delete this sermon");
        }

        sermonRepository.delete(sermon);
    }

}