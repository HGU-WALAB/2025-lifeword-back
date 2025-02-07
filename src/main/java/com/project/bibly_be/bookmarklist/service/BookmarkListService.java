package com.project.bibly_be.bookmarklist.service;

import com.project.bibly_be.bible.dto.BibleResponseDTO;
import com.project.bibly_be.bible.entity.Bible;
import com.project.bibly_be.bookmark.repository.BookmarkRepository;
import com.project.bibly_be.user.entity.User;
import com.project.bibly_be.bible.repository.BibleRepository;
import com.project.bibly_be.bookmarklist.dto.BookmarkListRequestDTO;
import com.project.bibly_be.bookmarklist.dto.BookmarkListResponseDTO;
import com.project.bibly_be.bookmarklist.dto.BookmarkListUserResponseDTO;
import com.project.bibly_be.bookmarklist.entity.BookmarkList;
import com.project.bibly_be.bookmarklist.repository.BookmarkListRepository;
//import com.project.bibly_be.bookmarklist.repository.UserRepository;

import com.project.bibly_be.bookmarklist.util.BookmarkListUtil;
import com.project.bibly_be.sermon.dto.SermonResponseDTO;
import com.project.bibly_be.sermon.entity.Sermon;
import com.project.bibly_be.sermon.repository.SermonRepository;
import com.project.bibly_be.sermon.service.SermonService;
import com.project.bibly_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkListService {
    private final BookmarkListRepository bookmarkListRepository;
    private final SermonRepository sermonRepository;
    private final BibleRepository bibleRepository;
    private final UserRepository userRepository;

    // create
    public BookmarkListUserResponseDTO createBookmarkList(BookmarkListRequestDTO bookmarkListRequestDTO) {
        List<Long> sermonList = new ArrayList<>();
        List<Long> sermonIds = bookmarkListRequestDTO.getSermonIds();
        for(Long sermonId : sermonIds){
            Sermon sermon = sermonRepository.findById(sermonId).isPresent() ? sermonRepository.findById(sermonId).get() : null;
            if(sermon != null){
                sermonList.add(sermonId);
            }
        }
        String sermonIdsList = BookmarkListUtil.listToJson(sermonList);

        List<Long> verseList = new ArrayList<>();
        List<Long> verseIds = bookmarkListRequestDTO.getVerseIds();
        for(Long verseId : verseIds){
            Bible bible = bibleRepository.findById(verseId).isPresent()? bibleRepository.findById(verseId).get() : null;
            if(bible!=null){
                verseList.add(verseId);
            }
        }
        String verseIdsList = BookmarkListUtil.listToJson(verseList);

        User user = userRepository.findById(bookmarkListRequestDTO.getUserId()).orElseThrow(() -> new UsernameNotFoundException("user not found"));

        BookmarkList bookmarkList = new BookmarkList(user, bookmarkListRequestDTO.getName(),verseIdsList, sermonIdsList);
        //BookmarkList bookmarkList =new BookmarkList(user,bookmarkListRequestDTO);

        bookmarkListRepository.save(bookmarkList);
        return BookmarkListUserResponseDTO.from(bookmarkList);

    }

    //update





    // getAll only return bookmark names and ids belongs to user
    public List<BookmarkListUserResponseDTO>  getBookmarkListByUser(UUID userId){

        List<BookmarkList> bookmarkLists = bookmarkListRepository.findByUserId(userId);
        if(bookmarkLists.isEmpty()) return new ArrayList<>();

        // return bookmarkList ids& names

        return bookmarkLists.stream().map(
                BookmarkListUserResponseDTO::from
        ).collect(Collectors.toList());
    }




    // getBookmarkByUser
    public BookmarkListResponseDTO getBookmarkByUser(UUID userId,Long id){

        BookmarkList bookmarkList = bookmarkListRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("list not found"));

        //check user has this bookmark (later if it can be shared by other, than this step will be deleted) or add column private/public
        if(!bookmarkList.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("user cant access to this bookmark");
        }

        List<Sermon> sermonList = new ArrayList<>();
        List<Long> sermonIds = BookmarkListUtil.jsonToList(bookmarkList.getSermonIds());
        for(Long sermonId : sermonIds){
            Sermon sermon = sermonRepository.findById(sermonId).isPresent() ? sermonRepository.findById(sermonId).get() : null;
            if(sermon != null){
                sermonList.add(sermon);
            }
        }

        List<Bible> verseList = new ArrayList<>();
        List<Long> verseIds = BookmarkListUtil.jsonToList(bookmarkList.getVerseIds());
        for(Long verseId : verseIds){
            Bible bible = bibleRepository.findById(verseId).isPresent() ? bibleRepository.findById(verseId).get() : null;
            if(bible != null){
                verseList.add(bible);
            }
        }

        //List mapping
        List<SermonResponseDTO> sermonResponseDTOList = sermonList.stream().map(
                sermon -> SermonResponseDTO.builder()
                        .sermonId(sermon.getSermonId())
                        .ownerName(sermon.getOwner().getName())
                        .createdAt(sermon.getCreatedAt())
                        .updatedAt(sermon.getUpdatedAt())
                        .isPublic(sermon.isPublic())
                        .worshipType(sermon.getWorshipType())
                        .mainScripture(sermon.getMainScripture())
                        .additionalScripture(sermon.getAdditionalScripture())
                        .summary(sermon.getSummary())
                        .sermonDate(sermon.getSermonDate())
                        .sermonTitle(sermon.getSermonTitle())
                        .notes(sermon.getNotes())
                        .recordInfo(sermon.getRecordInfo())
                        .fileCode(sermon.getFileCode())
                        .userId(sermon.getOwner().getId())
                        .build()
        ).collect(Collectors.toList());

        List<BibleResponseDTO> verseResponseDTOList = verseList.stream().map(
                BibleResponseDTO::from
        ).collect(Collectors.toList());

        // bookmarkList, sermonDTO list, verseDTO list
        return BookmarkListResponseDTO.from(bookmarkList ,sermonResponseDTOList,verseResponseDTOList);
    }


    // getListByIdSermon
    public BookmarkListResponseDTO getBookmarkSermonByUser(UUID userId,Long id){
        BookmarkList bookmarkList = bookmarkListRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("list not found"));

        //check user has this bookmark (later if it can be shared by other, than this step will be deleted) or add column private/public
        if(!bookmarkList.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("user cant access to this bookmark");
        }

        List<Sermon> sermonList = new ArrayList<>();
        List<Long> sermonIds = BookmarkListUtil.jsonToList(bookmarkList.getSermonIds());
        for(Long sermonId : sermonIds){
            Sermon sermon = sermonRepository.findById(sermonId).isPresent() ? sermonRepository.findById(sermonId).get() : null;
            if(sermon != null){
                sermonList.add(sermon);
            }
        }
        //List mapping
        List<SermonResponseDTO> sermonResponseDTOList = sermonList.stream().map(
                sermon -> SermonResponseDTO.builder()
                        .sermonId(sermon.getSermonId())
                        .ownerName(sermon.getOwner().getName())
                        .createdAt(sermon.getCreatedAt())
                        .updatedAt(sermon.getUpdatedAt())
                        .isPublic(sermon.isPublic())
                        .worshipType(sermon.getWorshipType())
                        .mainScripture(sermon.getMainScripture())
                        .additionalScripture(sermon.getAdditionalScripture())
                        .summary(sermon.getSummary())
                        .sermonDate(sermon.getSermonDate())
                        .sermonTitle(sermon.getSermonTitle())
                        .notes(sermon.getNotes())
                        .recordInfo(sermon.getRecordInfo())
                        .fileCode(sermon.getFileCode())
                        .userId(sermon.getOwner().getId())
                        .build()
        ).collect(Collectors.toList());

        return BookmarkListResponseDTO.sermon(bookmarkList ,sermonResponseDTOList);

    }


    // getListByIdVerse
    public BookmarkListResponseDTO getBookmarkVerseByUser(UUID userId,Long id){
        BookmarkList bookmarkList = bookmarkListRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("list not found"));

        //check user has this bookmark (later if it can be shared by other, than this step will be deleted) or add column private/public
        if(!bookmarkList.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("user cant access to this bookmark");
        }

        List<Bible> verseList = new ArrayList<>();
        List<Long> verseIds = BookmarkListUtil.jsonToList(bookmarkList.getVerseIds());
        for(Long verseId : verseIds){
            Bible bible = bibleRepository.findById(verseId).isPresent() ? bibleRepository.findById(verseId).get() : null;
            if(bible != null){
                verseList.add(bible);
            }
        }

        List<BibleResponseDTO> verseResponseDTOList = verseList.stream().map(
                BibleResponseDTO::from
        ).collect(Collectors.toList());

        // bookmarkList, sermonDTO list, verseDTO list
        return BookmarkListResponseDTO.bible(bookmarkList ,verseResponseDTOList);

    }


    // delete
    public void deleteBookmarkByUser(UUID userId,Long id){
        BookmarkList bookmarkList = bookmarkListRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("list not found"));

        //check user has this bookmark (later if it can be shared by other, than this step will be deleted) or add column private/public
        if(!bookmarkList.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("user cant access to this bookmark");
        }
        bookmarkListRepository.deleteById(id);

    }


}
