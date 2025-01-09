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
import java.util.stream.Collectors;@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BibleService {
    private final BibleRepository bibleRepository;

    public List<BibleResponseDTO> search(String keyword1) {
        List<Bible> searchResults;

        // "창세기 1장 1절"
        if (keyword1.matches("\\S+\\s*\\d+장\\s*\\d+절")) {
            searchResults = handleSpecificVerse(keyword1);
        }
        // 비정형 입력 (예: "창세기 1:1", "창세기 1장", "창세기 1장 1절~10절")
        else if (keyword1.matches("\\S+\\s*\\d+[:장]\\s*\\d+.*|\\S+\\s*\\d+장.*")) {
            searchResults = parseReferenceAndSearch(keyword1);
        }
        // 특정 책 검색 (예: "창세기", "요한복음")
        else if (bibleRepository.findFirstByShortLabelOrLongLabel(keyword1, keyword1).isPresent()) {
            searchResults = bibleRepository.findByTestamentOrBook(keyword1);
        }
        // 단어 검색
        else {
            searchResults = bibleRepository.searchByVerse(keyword1);
        }

        return searchResults.stream()
                .map(BibleResponseDTO::from)
                .collect(Collectors.toList());
    }

    private List<Bible> handleSpecificVerse(String reference) { //여러 조건이 있다보니 "창세기 1장 1절" 이 검색이 되지 않아 메소드를 따로 만듬.
        // 정규표현식으로 책 이름, 장, 절 추출
        String pattern = "^(\\S+)\\s*(\\d+)장\\s*(\\d+)절$";
        Matcher matcher = Pattern.compile(pattern).matcher(reference);

        if (matcher.find()) {
            String bookName = matcher.group(1); // 책 이름
            int chapter = Integer.parseInt(matcher.group(2)); // 장
            int verse = Integer.parseInt(matcher.group(3)); // 절

            // 책 번호 찾기
            Bible bible = bibleRepository.findFirstByShortLabelOrLongLabel(bookName, bookName).orElse(null);
            if (bible == null) {
                return new ArrayList<>();
            }

            // 해당 절만 반환
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
        // 정규표현식 패턴들
        String pattern1 = "^(\\S+)\\s*(\\d+)[장:](\\d+)[-~]?(\\d+)?$"; // ", "창세기 1:1"
        String pattern2 = "^(\\S+)\\s*(\\d+)장[-~]?$"; // "창세기 1장"
        String pattern3 = "^(\\S+)\\s*(\\d+)장\\s*(\\d+)절[-~](\\d+)절?$"; // "창세기 1장 1절~10절"

        Matcher matcher1 = Pattern.compile(pattern1).matcher(reference);
        Matcher matcher2 = Pattern.compile(pattern2).matcher(reference);
        Matcher matcher3 = Pattern.compile(pattern3).matcher(reference);

        String bookName;
        int chapter;
        Integer startVerse = null; // 구절 시작 번호
        Integer endVerse = null;   // 구절 끝 번호

        if (matcher3.find()) {
            // "창세기 1장 1절~10절"
            bookName = matcher3.group(1);
            chapter = Integer.parseInt(matcher3.group(2));
            startVerse = Integer.parseInt(matcher3.group(3));
            endVerse = Integer.parseInt(matcher3.group(4));
        } else if (matcher1.find()) {
            // "창세기 1장 1절" 또는 "창세기 1:1"
            bookName = matcher1.group(1);
            chapter = Integer.parseInt(matcher1.group(2));
            startVerse = Integer.parseInt(matcher1.group(3));
            endVerse = matcher1.group(4) != null ? Integer.parseInt(matcher1.group(4)) : startVerse;
        } else if (matcher2.find()) {
            // "창세기 1장"
            bookName = matcher2.group(1);
            chapter = Integer.parseInt(matcher2.group(2));
        } else {
            return new ArrayList<>();
        }

        // 책 번호 찾기
        Bible bible = bibleRepository.findFirstByShortLabelOrLongLabel(bookName, bookName)
                .orElse(null);
        if (bible == null) {
            return new ArrayList<>();
        }

        if (startVerse != null && endVerse != null) {
            // 특정 절 범위 검색
            return bibleRepository.findByBookAndChapterAndParagraphBetween(
                    bible.getBook(),
                    chapter,
                    startVerse,
                    endVerse
            );
        } else {
            // 특정 장 전체 검색
            return bibleRepository.findByTestamentAndBookAndChapter(bible.getTestament(), bible.getBook(), chapter);
        }
    }
}
