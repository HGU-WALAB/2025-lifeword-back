package com.project.bibly_be.text.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TextResponse {
    private Long id;
    private Long sermonId;
    private String userId;
    private String textTitle;
    private boolean isDraft;
    private LocalDateTime textCreatedAt;
    private LocalDateTime textUpdatedAt;
    private String textContent;
}
