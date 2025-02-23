package com.project.bibly_be.text.service;

import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.sermon.repository.SermonRepository;
import com.project.bibly_be.text.dto.TextResponse;
import com.project.bibly_be.text.dto.TextSummary;
import com.project.bibly_be.text.entity.Text;
import com.project.bibly_be.text.repository.TextRepository;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.project.bibly_be.user.entity.Role.ADMIN;
import static com.project.bibly_be.user.entity.Role.USER;

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

    public List<TextSummary> getTextSummariesForSermon(Long sermonId, String userId) {
        UUID uuid = UUID.fromString(userId);
        User user = userRepository.findById(uuid).orElseThrow(() -> new RuntimeException("User not found"));
        if(user.getIsAdmin()){// if admin user , get all ,
            List<Text> texts = textRepository.findBySermon_SermonId(sermonId);
            return texts.stream()
                    .map(this::convertToSummaryDto)
                    .collect(Collectors.toList());
        }
        // if not admin user, use findBySermonIdAndVisibility
        List<Text> texts = textRepository.findBySermonIdAndVisibility(sermonId, uuid);
        return texts.stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }

    public TextResponse getTextDetail(Long sermonId, Long textId, String userId) {
        Text text = textRepository.findById(textId)
                .orElseThrow(() -> new RuntimeException("Text not found"));

        // TEXT 가 해당 SEREMON 에 있는건지 확인
        if (!text.getSermon().getSermonId().equals(sermonId)) {
            throw new RuntimeException("Text does not belong to the specified sermon.");
        }

        // If draft, check that the user is owner of text or admin (admin 은 수정 삭제 다 가능)
        User currentUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (text.isDraft() && !text.getUser().getId().toString().equals(userId)
                && (currentUser.getRole() == null || currentUser.getRole()==USER)) {
            throw new RuntimeException("Unauthorized: You do not have permission to view this draft.");
        }

        return convertToResponseDto(text);
    }

    public Text patchText(Long textId, String userId, String textTitle, boolean isDraft, String textContent) {
        Text text = textRepository.findById(textId)
                .orElseThrow(() -> new RuntimeException("Text not found"));

        User currentUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // admin 허용
        if (!text.getUser().getId().toString().equals(userId) &&
                (currentUser.getRole() == null || currentUser.getRole()==USER)) {
            throw new RuntimeException("Unauthorized: You do not have permission to update this text.");
        }


        text.setTextTitle(textTitle);
        text.setDraft(isDraft);
        if(textContent != null) {
            text.setTextContent(textContent);
        }
        return textRepository.save(text);
    }

    public void deleteText(Long textId, String userId) {
        Text text = textRepository.findById(textId)
                .orElseThrow(() -> new RuntimeException("Text not found"));

        User currentUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // admin 허용
        if (!text.getUser().getId().toString().equals(userId) &&
                (currentUser.getRole() == null || currentUser.getRole()==USER)) {
            throw new RuntimeException("Unauthorized: You do not have permission to delete this text.");
        }

        textRepository.delete(text);
    }

    private TextResponse convertToResponseDto(Text text) {
        TextResponse dto = new TextResponse();
        dto.setId(text.getId());
        dto.setUserName(text.getUser().getName());
        dto.setSermonId(text.getSermon().getSermonId());
        dto.setUserId(text.getUser().getId().toString());
        dto.setTextTitle(text.getTextTitle());
        dto.setDraft(text.isDraft());
        dto.setTextContent(text.getTextContent());
        dto.setTextCreatedAt(text.getTextCreatedAt());
        dto.setTextUpdatedAt(text.getTextUpdatedAt());
        return dto;
    }

    // 짧은 list
    private TextSummary convertToSummaryDto(Text text) {
        TextSummary dto = new TextSummary();
        dto.setId(text.getId());
        dto.setSermonId(text.getSermon().getSermonId());
        dto.setUserId(text.getUser().getId().toString());
        dto.setUserName(text.getUser().getName());
        dto.setTextTitle(text.getTextTitle());
        dto.setDraft(text.isDraft());
        dto.setTextCreatedAt(text.getTextCreatedAt());
        dto.setTextUpdatedAt(text.getTextUpdatedAt());
        return dto;
    }
}
