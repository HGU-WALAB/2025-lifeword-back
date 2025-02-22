package com.project.bibly_be.text.entity;

import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "texts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Text {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sermon_id", nullable = false)
    private Sermon sermon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String textTitle;

    @Column(nullable = false)
    private boolean isDraft;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String textContent;
}
