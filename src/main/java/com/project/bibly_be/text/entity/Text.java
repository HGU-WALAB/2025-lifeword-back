package com.project.bibly_be.text.entity;

import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "text_created_at", nullable = false, updatable = false)
    private LocalDateTime textCreatedAt;

    @Column(name = "text_updated_at", nullable = false)
    private LocalDateTime textUpdatedAt;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String textContent;

    @PrePersist
    public void prePersist() {
        this.textCreatedAt = LocalDateTime.now();
        this.textUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.textUpdatedAt = LocalDateTime.now();
    }
}
