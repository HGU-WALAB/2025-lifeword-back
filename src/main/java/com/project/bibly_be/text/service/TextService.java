package com.project.bibly_be.text.service;

import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.sermon.repository.SermonRepository;
import com.project.bibly_be.text.entity.Text;
import com.project.bibly_be.text.repository.TextRepository;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
                .orElseThrow(() -> new RuntimeException("설교 찾지 못함"));

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("유저 찾지 못함"));

        Text text = Text.builder()
                .sermon(sermon)
                .user(user)
                .textTitle(textTitle)
                .isDraft(isDraft)
                .textContent(textContent)
                .build();

        textRepository.save(text);
    }
}
