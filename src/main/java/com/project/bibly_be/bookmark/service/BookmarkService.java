package com.project.bibly_be.bookmark.service;

import com.project.bibly_be.bookmark.dto.BookmarkRequestDTO;
import com.project.bibly_be.bookmark.dto.BookmarkResponseDTO;
import com.project.bibly_be.bible.entity.Bible;
import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.bookmark.entity.Bookmark;
import com.project.bibly_be.sermon.repository.SermonRepository;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.bible.repository.BibleRepository;
import com.project.bibly_be.bookmark.repository.BookmarkRepository;
import com.project.bibly_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BibleRepository bibleRepository;
    private final SermonRepository sermonRepository;

    public BookmarkResponseDTO createBookmark(UUID userId, BookmarkRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // if same sermon/ verse exist in user bookmark already


        if(request.getIsSermon()){
            // get sermon in sermon table
            Sermon sermon = sermonRepository.findById(request.getSermonId())
                    .orElseThrow(()->new IllegalArgumentException("Sermon not found"));
            // check same bookmark exist
            Bookmark exist = bookmarkRepository.findByUserAndSermon_SermonId(user, request.getSermonId());
            if(exist != null){
                throw new IllegalArgumentException("Sermon already exist");
            }
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .verse(null) //verseId
                    .sermon(sermon) //sermon id
                    .isSermon(request.getIsSermon())
                    .build();

            Bookmark savedBookmark = bookmarkRepository.save(bookmark);
            return BookmarkResponseDTO.from(savedBookmark);
        }
        else{
            Bible verse = bibleRepository.findById(request.getVerseId())
                    .orElseThrow(() -> new IllegalArgumentException("Verse not found"));

            // check same bookmark exist
            Bookmark exist = bookmarkRepository.findByUserAndVerseIdx(user, request.getVerseId());
            if(exist != null){
                throw new IllegalArgumentException("verse already exist");
            }
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .verse(verse) //verseId
                    .sermon(null) //sermon id
                    .isSermon(request.getIsSermon())
                    .build();

            Bookmark savedBookmark = bookmarkRepository.save(bookmark);
            return BookmarkResponseDTO.from(savedBookmark);
        }

    }

    @Transactional(readOnly = true)
    public List<BookmarkResponseDTO> getBookmarks(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Bookmark> bookmarks = bookmarkRepository.findAllByUser(user);
        return bookmarks.stream()
                .map(BookmarkResponseDTO::from)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<BookmarkResponseDTO> getUserBookmarksSermon(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Bookmark> bookmarks = bookmarkRepository.findAllWithSermonContentsByUser(user);
        return bookmarks.stream()
                .map(BookmarkResponseDTO::from)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<BookmarkResponseDTO> getUserBookmarksVerse(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Bookmark> bookmarks = bookmarkRepository.findAllVerseByUser(user);
        return bookmarks.stream()
                .map(BookmarkResponseDTO::from)
                .collect(Collectors.toList());

    }

    public void deleteBookmark(UUID userId, Long verseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        bookmarkRepository.deleteByUserAndVerseIdx(user, verseId);
    }
}