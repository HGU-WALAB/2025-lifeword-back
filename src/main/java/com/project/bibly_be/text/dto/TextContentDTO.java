package com.project.bibly_be.text.dto;

import com.project.bibly_be.text.entity.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TextContentDTO {
    private Long id;
    private Long sermonId;
    private String userName;
    private String textTitle;
    private boolean isDraft;
    private String textContent;

    // 정적 팩토리 메서드
    public static TextContentDTO from(Text text) {
        if (text == null) return null;

        return new TextContentDTO(
                text.getId(),
                text.getSermon().getSermonId(),
                text.getUser().getName(),
                text.getTextTitle(),
                true, // 또는 text.getIsPublic() 등 필요한 값으로 교체
                text.getTextContent()
        );
    }
}
