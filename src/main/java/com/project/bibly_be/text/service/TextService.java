package com.project.bibly_be.text.service;

import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.sermon.repository.SermonRepository;
import com.project.bibly_be.text.dto.TextResponseDTO;
import com.project.bibly_be.text.entity.Text;
import com.project.bibly_be.text.repository.TextRepository;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TextService {

    private final TextRepository textRepository;
    private final SermonRepository sermonRepository;
    private final UserRepository userRepository;

    @Autowired
    public TextService(TextRepository textRepository,
                       SermonRepository sermonRepository,
                       UserRepository userRepository) {
        this.textRepository = textRepository;
        this.sermonRepository = sermonRepository;
        this.userRepository = userRepository;
    }

    public void createText(Long sermonId, String userId, boolean isDraft, String textTitle, String textContent) {
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new RuntimeException("Sermon not found"));

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        Text text = Text.builder()
                .sermon(sermon)
                .user(user)
                .textTitle(textTitle)
                .isDraft(isDraft)
                .textContent(textContent)
                .build();

        textRepository.save(text);
    }

    public List<TextResponseDTO> getTextsForSermon(Long sermonId, String userId) {
        UUID uuid = UUID.fromString(userId);
        List<Text> texts = textRepository.findBySermonIdAndVisibility(sermonId, uuid);
        return texts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TextResponseDTO convertToDto(Text text) {
        TextResponseDTO dto = new TextResponseDTO();
        dto.setId(text.getId());
        dto.setSermonId(text.getSermon().getSermonId());
        dto.setUserId(text.getUser().getId().toString());
        dto.setTextTitle(text.getTextTitle());
        dto.setDraft(text.isDraft());
        dto.setTextCreatedAt(text.getTextCreatedAt());
        dto.setTextUpdatedAt(text.getTextUpdatedAt());
        dto.setTextContent(text.getTextContent());
        return dto;
    }

}
