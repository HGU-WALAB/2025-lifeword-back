package com.project.bibly_be.text.dto;

import lombok.Data;

@Data
public class TextPatchRequest {
    private String textTitle;
    private String textContent;
    private Boolean isDraft;
}
