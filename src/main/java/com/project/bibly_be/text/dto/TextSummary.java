package com.project.bibly_be.text.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TextSummary {
    private Long id;
    private Long sermonId;
    private String userId;
    private String userName;
    private String textTitle;
    private boolean isDraft;
    private LocalDateTime textCreatedAt;
    private LocalDateTime textUpdatedAt;
}
//textContent 없음