package com.project.bibly_be.text.dto;

import lombok.Data;

@Data
public class TextRequest {
    private Long sermonId;
    private String userId;
    private String textTitle;
    private boolean isDraft;
    private String textContent;
}
