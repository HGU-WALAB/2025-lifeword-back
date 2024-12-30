package com.project.bibly_be.entity;

import javax.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks")
@Getter
@NoArgsConstructor
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verse_id")
    private Bible verse;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Bookmark(User user, Bible verse) {
        this.user = user;
        this.verse = verse;
        this.createdAt = LocalDateTime.now();
    }
}