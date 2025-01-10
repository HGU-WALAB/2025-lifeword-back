package com.project.bibly_be.sermon.service;

import com.project.bibly_be.sermon.exception.ResourceNotFoundException;
import com.project.bibly_be.sermon.dto.SermonRequestDTO;
import com.project.bibly_be.sermon.dto.SermonResponseDTO;
import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.sermon.repo.SermonRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SermonService {
    private final SermonRepo sermonRepo;

    public List<SermonResponseDTO> getAllSermons() {
        List<Sermon> sermons = sermonRepo.findAll();

        if (sermons.isEmpty()) {
            return Collections.emptyList();
        }

        return sermons.stream()
                .map(sermon -> SermonResponseDTO.builder()
                        .id(sermon.getId())
                        .owner(sermon.getOwner())
                        .title(sermon.getTitle())
                        .sermonDate(sermon.getSermonDate())
                        .bibleVerseIndexes(sermon.getBibleVerseIndexes())
                        .keywords(sermon.getKeywords())
                        .build())
                .collect(Collectors.toList());
    }

    public SermonResponseDTO getSermonDetails(Long id) {
        Sermon sermon = sermonRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sermon with ID " + id + " not found"));

        return SermonResponseDTO.builder()
                .id(sermon.getId())
                .owner(sermon.getOwner())
                .title(sermon.getTitle())
                .sermonDate(sermon.getSermonDate())
                .bibleVerseIndexes(sermon.getBibleVerseIndexes())
                .sermonContent(sermon.getSermonContent())
                .keywords(sermon.getKeywords())
                .build();
    }

    public void addSermon(SermonRequestDTO requestDTO) {
        Sermon sermon = new Sermon();
        sermon.setOwner(requestDTO.getOwner());
        sermon.setTitle(requestDTO.getTitle());
        sermon.setSermonDate(requestDTO.getSermonDate());
        sermon.setBibleVerseIndexes(requestDTO.getBibleVerseIndexes());
        sermon.setSermonContent(requestDTO.getSermonContent());
        sermon.setKeywords(requestDTO.getKeywords());

        sermonRepo.save(sermon);
    }
}
