package com.project.bibly_be.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookResponseDTO {
    private Integer book;
    private String long_label;
    private String short_label;
    private String testament;
}