package com.project.bibly_be.dto.response;

import com.project.bibly_be.entity.Bible;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BibleResponseDTO {
    private Long idx;
    private Integer cate;
    private Integer book;
    private Integer chapter;
    private Integer paragraph;
    private String sentence;
    private String testament;
    private String long_label;
    private String short_label;

    public static BibleResponseDTO from(Bible bible) {
        return BibleResponseDTO.builder()
                .idx(bible.getIdx())
                .cate(bible.getCate())
                .book(bible.getBook())
                .chapter(bible.getChapter())
                .paragraph(bible.getParagraph())
                .sentence(bible.getSentence())
                .testament(bible.getTestament())
                .long_label(bible.getLongLabel())
                .short_label(bible.getShortLabel())
                .build();
    }
}