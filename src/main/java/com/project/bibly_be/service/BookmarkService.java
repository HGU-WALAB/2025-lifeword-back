package com.project.bibly_be.service;

import com.project.bibly_be.dto.request.BookmarkRequestDTO;
import com.project.bibly_be.dto.response.BookmarkResponseDTO;
import com.project.bibly_be.entity.Bible;
import com.project.bibly_be.entity.Bookmark;
import com.project.bibly_be.entity.User;
import com.project.bibly_be.repository.BibleRepository;
import com.project.bibly_be.repository.BookmarkRepository;
import com.project.bibly_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BibleRepository bibleRepository;

    public BookmarkResponseDTO createBookmark(String kakaoUid, BookmarkRequestDTO request) {
        User user = userRepository.findByKakaoUid(kakaoUid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Bible verse = bibleRepository.findById(request.getVerseId())
                .orElseThrow(() -> new IllegalArgumentException("Verse not found"));

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .verse(verse)
                .build();

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        return BookmarkResponseDTO.from(savedBookmark);
    }

    @Transactional(readOnly = true)
    public List<BookmarkResponseDTO> getBookmarks(String kakaoUid) {
        User user = userRepository.findByKakaoUid(kakaoUid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return bookmarkRepository.findByUser(user).stream()
                .map(BookmarkResponseDTO::from)
                .collect(Collectors.toList());
    }

    public void deleteBookmark(String kakaoUid, Long verseId) {
        User user = userRepository.findByKakaoUid(kakaoUid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        bookmarkRepository.deleteByUserAndVerseIdx(user, verseId);
    }
}