package com.project.bibly_be.bookmarklist.entity;

import com.project.bibly_be.bible.entity.Bible;
import com.project.bibly_be.bookmarklist.dto.BookmarkListRequestDTO;
import com.project.bibly_be.bookmarklist.dto.BookmarkListUserResponseDTO;
import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.user.entity.User;
import javax.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "bookmarklist")
@Getter
@Setter
@NoArgsConstructor

public class BookmarkList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "verse_id", columnDefinition = "JSON")
    private String verseIds;

    @Column(name = "sermon_id", columnDefinition = "JSON")
    private String sermonIds;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;


    @Builder
    public BookmarkList(User user, String name, String verseIds, String sermonIds) {
        this.user = user;
        this.name = name;
        this.verseIds = verseIds;
        this.sermonIds = sermonIds;
        this.createdAt = LocalDateTime.now();
    }

//    @Builder
//    public BookmarkList(User user, BookmarkListRequestDTO requestDTO) {
//        this.user = user;
//        this.name = requestDTO.getName();
//        this.verseIds = requestDTO.getVerseIds();
//        this.sermonIds = requestDTO.getSermonIds();
//        this.createdAt = LocalDateTime.now();
//    }

}
