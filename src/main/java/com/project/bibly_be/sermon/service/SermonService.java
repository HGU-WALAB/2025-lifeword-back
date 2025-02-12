package com.project.bibly_be.sermon.service;

import com.project.bibly_be.sermon.dto.ContentDTO;
import com.project.bibly_be.sermon.dto.SermonRequestDTO;
import com.project.bibly_be.sermon.dto.SermonResponseDTO;
import com.project.bibly_be.sermon.dto.SermonResponsePageDTO;
import com.project.bibly_be.sermon.entity.Content;
import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.sermon.repository.ContentRepository;
import com.project.bibly_be.sermon.repository.SermonRepository;
import com.project.bibly_be.sermon.specification.SermonSpecification;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.ReturnTypeParser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
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
    private final ReturnTypeParser returnTypeParser;

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

    // GET all sermons ( for admin )
    public List<SermonResponseDTO> getAllSermons() {
        return sermonRepository.findAll().stream()
                .map(this::mapToSermonResponseDTO)
                .collect(Collectors.toList());
    }


    // GET all sermons by USER
    public List<SermonResponseDTO> getAllSermonsByUser(String userId) {
        UUID userUUID = UUID.fromString(userId);
        return sermonRepository.findByOwner_Id(userUUID).stream()
                .map(this::mapToSermonResponseDTO)
                .collect(Collectors.toList());
    }

    // GET private sermons by USER
    public List<SermonResponseDTO> getPrivateSermons(String userId) {
        UUID userUUID = UUID.fromString(userId);
        return sermonRepository.findByOwner_IdAndIsPublicFalse(userUUID).stream()
                .map(this::mapToSermonResponseDTO)
                .collect(Collectors.toList());
    }

    // GET public sermons by USER
    public List<SermonResponseDTO> getPublicSermonsByUser(String userId) {
        UUID userUUID = UUID.fromString(userId);
        return sermonRepository.findByOwner_IdAndIsPublicTrue(userUUID).stream()
                .map(this::mapToSermonResponseDTO)
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
                .userId(sermon.getOwner().getId()) // user's UUID -> userId :
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
    @Transactional
    public SermonResponseDTO updateSermonAndContent(Long sermonId, String loggedInUserId, SermonRequestDTO requestDTO) {
        // Fetch the sermon by ID
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new IllegalArgumentException("Sermon not found"));

        // Check if the logged-in user is the owner
        if (!sermon.getOwner().getId().toString().equals(loggedInUserId)) {
            throw new IllegalArgumentException("Unauthorized to update this sermon");
        }

        // Update sermon fields
        sermon.setSermonDate(LocalDate.parse(requestDTO.getSermonDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay());
        sermon.setWorshipType(requestDTO.getWorshipType());
        sermon.setMainScripture(requestDTO.getMainScripture());
        sermon.setAdditionalScripture(requestDTO.getAdditionalScripture());
        sermon.setSermonTitle(requestDTO.getSermonTitle());
        sermon.setSummary(requestDTO.getSummary());
        sermon.setNotes(requestDTO.getNotes());
        sermon.setRecordInfo(requestDTO.getRecordInfo());
        sermon.setPublic(requestDTO.isPublic());

        // Fetch content and update
        Content content = sermon.getContents().stream()
                .findFirst() // Assuming 1-to-1 relation between Sermon and Content
                .orElseThrow(() -> new IllegalArgumentException("Content not found for this sermon"));

        content.setContentText(requestDTO.getContentText());
        contentRepository.save(content);

        Sermon updatedSermon = sermonRepository.save(sermon);

        return mapToSermonResponseDTO(updatedSermon);
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

    public List<SermonResponseDTO> searchSermons(String keyword) {
        List<Sermon> results;

        // 작성자로 검색 (최우선)
        results = sermonRepository.searchByAuthorName(keyword);
        if (!results.isEmpty()) {
            return results.stream()
                    .map(this::mapToSermonResponseDTO)
                    .distinct()
                    .collect(Collectors.toList());
        }

        // 제목으로 검색 (작성자가 없을 경우)
        results = sermonRepository.searchBySermonTitle(keyword);
        if (!results.isEmpty()) {
            return results.stream()
                    .map(this::mapToSermonResponseDTO)
                    .distinct()
                    .collect(Collectors.toList());
        }

        // 본문 내용으로 검색 (작성자 & 제목이 없을 경우)
        results = sermonRepository.searchBySermonTitleOrContent(keyword);

        return results.stream()
                .map(this::mapToSermonResponseDTO)
                .distinct()
                .collect(Collectors.toList());
    }




    // Utility to map Sermon to SermonResponseDTO
    private SermonResponseDTO mapToSermonResponseDTO(Sermon sermon) {
        List<ContentDTO> contents = sermon.getContents() != null
                ? sermon.getContents().stream()
                .map(content -> ContentDTO.builder()
                        .contentId(content.getContentId())
                        .fileCode(content.getFileCode())
                        .contentText(content.getContentText())
                        .build())
                .collect(Collectors.toList())
                : Collections.emptyList();

        return SermonResponseDTO.builder()
                .sermonId(sermon.getSermonId())
                .ownerName(sermon.getOwner() != null ? sermon.getOwner().getName() : "Unknown Owner")
                .userId(sermon.getOwner() != null ? sermon.getOwner().getId() : null)  // Ensure userId is mapped correctly
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



    private Sort getSort(String sortOrder) {
        switch (sortOrder != null ? sortOrder.toLowerCase() : "desc") {
            case "asc":
                return Sort.by(Sort.Direction.ASC, "sermonDate");
            case "recent":
                return Sort.by(Sort.Direction.DESC, "updatedAt");
            case "desc":
            default:
                return Sort.by(Sort.Direction.DESC, "sermonDate");
        }
    }

    // JPA specification

    public SermonResponsePageDTO searchSermonFilterUser(String keyword,UUID userId,String sortOrder, List<String> worshipTypes,  String startDate, String endDate, List<String> scriptures, int page, int size, int mode){

        Pageable pageable = PageRequest.of(page-1, size, getSort(sortOrder)); // case asc, desc, recent <-- default is recent
        LocalDateTime start = null;
        LocalDateTime end = null;


        // 날짜 범위 처리
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (startDate != null && !startDate.isEmpty()) {
            start = LocalDate.parse(startDate, formatter).atStartOfDay();
        }
        if (endDate != null && !endDate.isEmpty()) {
            end = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);
        }
        //worshipTypes.set(0, "all".equalsIgnoreCase(worshipTypes.get(0)) ? null : worshipTypes.get(0));
        Specification<Sermon> spec = SermonSpecification.withFilters(userId,keyword,worshipTypes,start,end,scriptures,mode);

        Page<Sermon> result = sermonRepository.findAll(spec, pageable);

        return SermonResponsePageDTO.fromPage(result.map(this::mapToSermonResponseDTO));


    }


    //admin
    public SermonResponsePageDTO searchSermonsFiltered(String keyword,String sortOrder, List<String> worshipTypes,  String startDate, String endDate, List<String> scriptures, int page, int size) {
        //Pageable pageable =  PageRequest.of(page - 1, size);

        Pageable pageable = PageRequest.of(page-1, size, getSort(sortOrder));
        LocalDateTime start = null;
        LocalDateTime end = null;


        // 날짜 범위 처리
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (startDate != null && !startDate.isEmpty()) {
            start = LocalDate.parse(startDate, formatter).atStartOfDay();
        }
        if (endDate != null && !endDate.isEmpty()) {
            end = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);
        }

        Specification<Sermon> spec = SermonSpecification.withFilters(null,keyword,worshipTypes,start,end,scriptures,4);

        Page<Sermon> result = sermonRepository.findAll(spec, pageable);

        return SermonResponsePageDTO.fromPage(result.map(this::mapToSermonResponseDTO));
    }


}