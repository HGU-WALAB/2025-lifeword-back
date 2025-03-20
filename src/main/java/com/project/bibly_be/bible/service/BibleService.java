package com.project.bibly_be.bible.service;

import com.project.bibly_be.bible.dto.BibleResponseDTO;
import com.project.bibly_be.bible.dto.BookResponseDTO;
import com.project.bibly_be.bible.entity.Bible;
import com.project.bibly_be.bible.repository.BibleRepository;
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
    public List<BibleResponseDTO> search(String keyword1) {
        List<Bible> searchResults;

        if (keyword1.matches("\\S+\\s*\\d+장\\s*\\d+절")) {
            searchResults = handleSpecificVerse(keyword1);
        } else if (keyword1.matches("\\S+\\s*\\d+[:장]\\s*\\d+.*|\\S+\\s*\\d+장.*")) {
            searchResults = parseReferenceAndSearch(keyword1);
        } else if (bibleRepository.findFirstByShortLabelOrLongLabel(keyword1, keyword1).isPresent()) {
            searchResults = bibleRepository.findByTestamentOrBook(keyword1);
        } else {
            // 쉼표가 포함된 경우에도 `searchByWordWithPriority()`를 호출하도록 변경
            searchResults = searchByWordWithPriority(keyword1);
        }

        if (searchResults.isEmpty()) {
            throw new IllegalArgumentException("해당 범위에 구절이나 장이 없습니다.");
        }

        return searchResults.stream()
                .map(BibleResponseDTO::from)
                .collect(Collectors.toList());
    }

    private List<Bible> searchByWordWithPriority(String keyword) {
        String[] keywords = keyword.split("\\s*,\\s*"); // 쉼표 기준으로 키워드 분리

        if (keywords.length == 1) {
            return bibleRepository.searchByVerse(keyword); // 기존 단일 검색 로직 유지
        }

        // Step 1: AND 조건 필터링 (모든 키워드를 포함하는 구절)
        List<Bible> andFilteredResults = bibleRepository.searchByAllWords(keywords[0], keywords.length > 1 ? keywords[1] : null);

        // Step 2: OR 조건 필터링 (AND에서 찾은 구절을 제외한 나머지)
        List<Bible> orFilteredResults = bibleRepository.searchByAnyWords(keywords[0], keywords.length > 1 ? keywords[1] : null);

        // AND 결과를 제외한 OR 결과 필터링
        orFilteredResults.removeAll(andFilteredResults);

        // 최종 결과 리스트 (AND 먼저, OR 나중)
        List<Bible> finalResults = new ArrayList<>();
        finalResults.addAll(andFilteredResults);
        finalResults.addAll(orFilteredResults);

        //  검색 우선순위를 반영한 정렬 (idx 기준)
        finalResults.sort((b1, b2) -> {
            boolean isB1AndResult = andFilteredResults.contains(b1);
            boolean isB2AndResult = andFilteredResults.contains(b2);

            if (isB1AndResult && !isB2AndResult) {
                return -1;
            } else if (!isB1AndResult && isB2AndResult) {
                return 1;
            }

            return Long.compare(b1.getIdx(), b2.getIdx());
        });

        return finalResults;
    }


    private List<Bible> handleCommaSeparatedInput(String keyword) {
        String[] parts = keyword.split(",");
        List<Bible> results = new ArrayList<>();

        for (String part : parts) {
            part = part.trim();
            if (part.matches("\\S+\\s*\\d+[:장]\\s*\\d+.*")) {
                results.addAll(parseReferenceAndSearch(part));
            } else {
                results.addAll(bibleRepository.searchByVerse(part));
            }
        }

        return results;
    }

    private List<Bible> handleSpecificVerse(String reference) {
        String pattern = "^(\\S+)\\s*(\\d+)장\\s*(\\d+)절$";
        Matcher matcher = Pattern.compile(pattern).matcher(reference);

        if (matcher.find()) {
            String bookName = matcher.group(1);
            int chapter = Integer.parseInt(matcher.group(2));
            int verse = Integer.parseInt(matcher.group(3));

            Bible bible = bibleRepository.findFirstByShortLabelOrLongLabel(bookName, bookName).orElse(null);
            if (bible == null) {
                throw new IllegalArgumentException("해당 범위에 구절이나 장이 없습니다.");
            }

            return bibleRepository.findByBookAndChapterAndParagraphBetween(
                    bible.getBook(),
                    chapter,
                    verse,
                    verse
            );
        }

        return new ArrayList<>();
    }

    private List<Bible> parseReferenceAndSearch(String reference) {
        String pattern1 = "^(\\S+)\\s*(\\d+)[\\s:장]\\s*(\\d+)[-~]?(\\d+)?$"; // "창 1:1", "창세기 1 장 1절", "창 1 :1"
        String pattern2 = "^(\\S+)\\s*(\\d+)장[-~]?$"; // "창세기 1장"
        String pattern3 = "^(\\S+)\\s*(\\d+)장\\s*(\\d+)절[-~](\\d+)절?$"; // "창세기 1장 1절~10절"

        Matcher matcher1 = Pattern.compile(pattern1).matcher(reference);
        Matcher matcher2 = Pattern.compile(pattern2).matcher(reference);
        Matcher matcher3 = Pattern.compile(pattern3).matcher(reference);

        String bookName;
        int chapter;
        Integer startVerse = null;
        Integer endVerse = null;

        if (matcher3.find()) {
            // "창세기 1장 1절~10절"
            bookName = matcher3.group(1);
            chapter = Integer.parseInt(matcher3.group(2));
            startVerse = Integer.parseInt(matcher3.group(3));
            endVerse = Integer.parseInt(matcher3.group(4));
        } else if (matcher1.find()) {
            // "창세기 1장 1절", "창 1:1", "창 1 :1"
            bookName = matcher1.group(1);
            chapter = Integer.parseInt(matcher1.group(2));
            startVerse = Integer.parseInt(matcher1.group(3));
            endVerse = matcher1.group(4) != null ? Integer.parseInt(matcher1.group(4)) : startVerse;
        } else if (matcher2.find()) {
            // "창세기 1장"
            bookName = matcher2.group(1);
            chapter = Integer.parseInt(matcher2.group(2));
        } else {
            throw new IllegalArgumentException("해당 범위에 구절이나 장이 없습니다.");
        }

        Bible bible = bibleRepository.findFirstByShortLabelOrLongLabel(bookName, bookName).orElse(null);
        if (bible == null) {
            throw new IllegalArgumentException("해당 범위에 구절이나 장이 없습니다.");
        }

        if (startVerse != null && endVerse != null) {
            return bibleRepository.findByBookAndChapterAndParagraphBetween(
                    bible.getBook(),
                    chapter,
                    startVerse,
                    endVerse
            );
        } else {
            return bibleRepository.findByTestamentAndBookAndChapter(bible.getTestament(), bible.getBook(), chapter);
        }
    }
}
