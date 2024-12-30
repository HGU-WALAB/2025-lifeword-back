package com.project.bibly_be.service;

import com.project.bibly_be.dto.response.BibleResponseDTO;
import com.project.bibly_be.dto.response.BookResponseDTO;
import com.project.bibly_be.entity.Bible;
import com.project.bibly_be.repository.BibleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BibleService {
    private final BibleRepository bibleRepository;

    public List<BibleResponseDTO> getBibles(String testament, Integer book, Integer chapter) {
        List<Bible> bibles;
        if (book == null) {
            bibles = bibleRepository.findByTestament(testament);
        } else if (chapter == null) {
            bibles = bibleRepository.findByTestamentAndBook(testament, book);
        } else {
            bibles = bibleRepository.findByTestamentAndBookAndChapter(testament, book, chapter);
        }
        return bibles.stream()
                .map(BibleResponseDTO::from)
                .collect(Collectors.toList());
    }

    public List<BookResponseDTO> getBooks(String testament) {
        List<Object[]> books = bibleRepository.findBooksByTestament(testament);
        return books.stream()
                .map(book -> BookResponseDTO.builder()
                        .book((Integer) book[0])
                        .long_label((String) book[1])
                        .short_label((String) book[2])
                        .testament((String) book[3])
                        .build())
                .collect(Collectors.toList());
    }

    public List<BibleResponseDTO> search(String mode, String keyword1, String keyword2, String operator) {
        List<Bible> searchResults;

        switch (mode) {
            case "verse":
                if (keyword2 == null) {
                    searchResults = bibleRepository.searchByVerse(keyword1);
                } else {
                    searchResults = "OR".equals(operator)
                            ? bibleRepository.searchByVerseWithOr(keyword1, keyword2)
                            : bibleRepository.searchByVerseWithAnd(keyword1, keyword2);
                }
                break;
            case "reference":
                // 장절 검색 로직 구현 (예: "창 1:1", "창세기 1장 1절")
                searchResults = parseReferenceAndSearch(keyword1);
                break;
            case "word":
                // 정확한 단어 매칭 검색
                searchResults = bibleRepository.searchByVerse(keyword1);
                break;
            default:
                throw new IllegalArgumentException("Invalid search mode");
        }

        return searchResults.stream()
                .map(BibleResponseDTO::from)
                .collect(Collectors.toList());
    }

    private List<Bible> parseReferenceAndSearch(String reference) {
        // 정규표현식 패턴들
        String pattern1 = "^(\\S+)\\s*(\\d+)[:장]\\s*(\\d+)[-~]?(\\d+)?$"; // "창 1:1" or "창세기 1:1" or "창 1:1-10"
        String pattern2 = "^(\\S+)\\s*(\\d+)장\\s*(\\d+)절[-~]?(\\d+)?절?$"; // "창세기 1장 1절" or "창세기 1장 1-10절"

        Matcher matcher1 = Pattern.compile(pattern1).matcher(reference);
        Matcher matcher2 = Pattern.compile(pattern2).matcher(reference);

        String bookName;
        int chapter;
        int startVerse;
        int endVerse;

        if (matcher1.find()) {
            bookName = matcher1.group(1);
            chapter = Integer.parseInt(matcher1.group(2));
            startVerse = Integer.parseInt(matcher1.group(3));
            endVerse = matcher1.group(4) != null ? Integer.parseInt(matcher1.group(4)) : startVerse;
        } else if (matcher2.find()) {
            bookName = matcher2.group(1);
            chapter = Integer.parseInt(matcher2.group(2));
            startVerse = Integer.parseInt(matcher2.group(3));
            endVerse = matcher2.group(4) != null ? Integer.parseInt(matcher2.group(4)) : startVerse;
        } else {
            return new ArrayList<>();
        }

        // 책 번호 찾기
        Bible bible = bibleRepository.findFirstByShortLabelOrLongLabel(bookName, bookName)
                .orElse(null);
        if (bible == null) {
            return new ArrayList<>();
        }

        // 해당 범위의 구절들 검색
        return bibleRepository.findByBookAndChapterAndParagraphBetween(
                bible.getBook(),
                chapter,
                startVerse,
                endVerse
        );
    }
}
