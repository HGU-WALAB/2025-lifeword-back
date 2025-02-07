package com.project.bibly_be.bookmarklist.controller;


import com.project.bibly_be.bible.dto.ApiResponseDTO;
import com.project.bibly_be.bookmark.service.BookmarkService;
import com.project.bibly_be.bookmarklist.dto.BookmarkListRequestDTO;
import com.project.bibly_be.bookmarklist.dto.BookmarkListResponseDTO;
import com.project.bibly_be.bookmarklist.dto.BookmarkListUserResponseDTO;
import com.project.bibly_be.bookmarklist.entity.BookmarkList;
import com.project.bibly_be.bookmarklist.service.BookmarkListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookmarklist")
@RequiredArgsConstructor
public class BookmarkListController {
    private final BookmarkListService bookmarkListService;

    // create (userId, name ,verseIds, sermonIds)
    @Operation(summary = "create book mark list")
    @PostMapping
    public BookmarkListUserResponseDTO createBookmarkList(
            @RequestBody BookmarkListRequestDTO bookmarkListRequestDTO
    ) {
        try{
            return bookmarkListService.createBookmarkList(bookmarkListRequestDTO);
        }catch (Exception e ){
            throw new RuntimeException(e);
        }
    }


    // update (userId, id, name, verseIds, sermonIds)


    // get All ( userId,id) both Sermon, Verse
    @Operation(summary = "get all bookmarkLists")
    @GetMapping("/getBookmarksListUser")
    public List<BookmarkListUserResponseDTO> getBookmarkListByUser(
            @RequestParam("userId") UUID userId
    ) {
        try{
           return bookmarkListService.getBookmarkListByUser(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //get single bookmarklist by id
    @Operation(summary = "get a single bookmarklist by using its id")
    @GetMapping("/getBookmarkUser")
    public BookmarkListResponseDTO getBookmarkByUser(
            @RequestParam("user_id") UUID userId,
            @RequestParam("id") Long id
    ){
        try {
            return bookmarkListService.getBookmarkByUser(userId,id);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


//    // getSermon ( userId , name , id )
    @Operation(summary = "get sermon bookmark")
    @GetMapping("/getBookmarkSermonUser")
    public BookmarkListResponseDTO getBookmarkBySermon(
            @RequestParam("user_id") UUID user_id,
            @RequestParam("id") Long id
    ){
        try{
            return bookmarkListService.getBookmarkSermonByUser(user_id,id);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

//    // getVerse  ( userId, name , id)
    @Operation(summary = "get verse")
    @GetMapping("/getBookmarkVerseUser")
    public BookmarkListResponseDTO getBookmarkByVerse(
            @RequestParam("user_id") UUID user_id,
            @RequestParam("id") Long id
    ){
        try {
            return bookmarkListService.getBookmarkVerseByUser(user_id,id);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // delete (userId, id)
    @Operation(summary = "delete bookmarklists")
    @DeleteMapping("/{id}")
    public ApiResponseDTO<Void> deleteBookmarkList(
            @RequestParam("user_id") UUID user_id,
            @PathVariable Long id
    ){
        bookmarkListService.deleteBookmarkByUser(user_id,id);
        return ApiResponseDTO.success("북마크 삭제 완료", null);
    }





}
