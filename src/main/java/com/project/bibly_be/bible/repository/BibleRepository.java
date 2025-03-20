package com.project.bibly_be.bible.repository;

import com.project.bibly_be.bible.entity.Bible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BibleRepository extends JpaRepository<Bible, Long> {
    List<Bible> findByTestament(String testament);
    List<Bible> findByTestamentAndBook(String testament, Integer book);
    List<Bible> findByTestamentAndBookAndChapter(String testament, Integer book, Integer chapter);
    Optional<Bible> findFirstByShortLabelOrLongLabel(String shortLabel, String longLabel);
    List<Bible> findByBookAndChapterAndParagraphBetween(Integer book, Integer chapter, Integer startParagraph, Integer endParagraph);

    @Query("SELECT DISTINCT b.book, b.longLabel, b.shortLabel, b.testament FROM Bible b WHERE b.testament = :testament")
    List<Object[]> findBooksByTestament(@Param("testament") String testament);

    @Query("SELECT b FROM Bible b WHERE b.sentence LIKE %:keyword%")
    List<Bible> searchByVerse(@Param("keyword") String keyword);

    @Query("SELECT b FROM Bible b WHERE b.longLabel = :keyword OR b.shortLabel = :keyword")
    List<Bible> findByTestamentOrBook(@Param("keyword") String keyword);

    /**
     * AND 검색: 입력된 모든 키워드가 포함된 구절 찾기
     */
    @Query("SELECT b FROM Bible b WHERE " +
            "(:keyword1 IS NULL OR LOWER(b.sentence) LIKE LOWER(CONCAT('%', :keyword1, '%'))) " +
            "AND (:keyword2 IS NULL OR LOWER(b.sentence) LIKE LOWER(CONCAT('%', :keyword2, '%')))")
    List<Bible> searchByAllWords(@Param("keyword1") String keyword1, @Param("keyword2") String keyword2);

    /**
     * OR 검색: 입력된 키워드 중 하나라도 포함된 구절 찾기
     */
    @Query("SELECT b FROM Bible b WHERE " +
            "LOWER(b.sentence) LIKE LOWER(CONCAT('%', :keyword1, '%')) " +
            "OR LOWER(b.sentence) LIKE LOWER(CONCAT('%', :keyword2, '%'))")
    List<Bible> searchByAnyWords(@Param("keyword1") String keyword1, @Param("keyword2") String keyword2);
}
