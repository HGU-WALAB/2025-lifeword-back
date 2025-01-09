package com.project.bibly_be.repository;

import com.project.bibly_be.entity.Bible;
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

    @Query("SELECT b FROM Bible b WHERE b.sentence LIKE %:keyword1% OR b.sentence LIKE %:keyword2%")
    List<Bible> searchByVerseWithOr(@Param("keyword1") String keyword1, @Param("keyword2") String keyword2);

    @Query("SELECT b FROM Bible b WHERE b.sentence LIKE %:keyword1% AND b.sentence LIKE %:keyword2%")
    List<Bible> searchByVerseWithAnd(@Param("keyword1") String keyword1, @Param("keyword2") String keyword2);
}
