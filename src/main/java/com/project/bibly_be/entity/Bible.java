package com.project.bibly_be.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bible2")
@Getter
@NoArgsConstructor
public class Bible {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private Integer cate;
    private Integer book;
    private Integer chapter;
    private Integer paragraph;
    private String sentence;
    private String testament;

    @Column(name = "long_label")
    private String longLabel;

    @Column(name = "short_label")
    private String shortLabel;
}