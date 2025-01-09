package com.project.bibly_be.sermon.entity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Sermon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String owner;
    private String title;
    private LocalDate sermonDate;

    @ElementCollection
    private List<Long> bibleVerseIndexes;

    @Lob
    private String sermonContent;

    @ElementCollection
    private List<String> keywords;
}
