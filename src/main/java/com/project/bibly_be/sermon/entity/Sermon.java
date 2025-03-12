package com.project.bibly_be.sermon.entity;
import com.project.bibly_be.user.entity.User;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sermons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sermon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sermonId;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;


    @Column(nullable = false)
    private LocalDateTime sermonDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isPublic;


    @Column(nullable = false)
    private String worshipType;

    @Column(nullable = false)
    private String mainScripture;

    private String additionalScripture;

    @Column(nullable = false)
    private String sermonTitle;

    @Column(length = 5000)
    private String summary;

    @Column(length = 5000)
    private String notes;

    private String recordInfo;

    @Column(nullable = false, unique = true)
    private String fileCode;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


