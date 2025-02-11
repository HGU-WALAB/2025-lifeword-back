package com.project.bibly_be.sermon.dto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
public class SermonResponsePageDTO {
    private final int totalPage;
    private final List<SermonResponseDTO> content;


    @Builder
    public SermonResponsePageDTO(List<SermonResponseDTO> content, int totalPage) {
        this.content = content;
        this.totalPage = totalPage;
    }


    public static SermonResponsePageDTO fromPage(Page<SermonResponseDTO> page) {
        return SermonResponsePageDTO.builder()
                .content(page.getContent())
                .totalPage(page.getTotalPages())
                .build();
    }
}
