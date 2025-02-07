package com.project.bibly_be.bookmarklist.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BookmarkListUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(BookmarkListUtil.class);

    public static List<Long> initProviderList() {
        return new ArrayList<>();
    }

    // JSON to List<Long>
    public static List<Long> jsonToList(String json) {
        if (json == null || json.isEmpty()) {
            return initProviderList();
        }
        try {
            List<Long> list = objectMapper.readValue(json, new TypeReference<List<Long>>() {});
            return (list.isEmpty()) ? initProviderList() : list;
        } catch (Exception e) {
            logger.error("Error while converting JSON to List<Long>: " + e.getMessage(), e);
            return initProviderList();
        }
    }

    // List<Long> to JSON
    public static String listToJson(List<Long> list) {
        if (list == null || list.isEmpty()) {
            list = initProviderList();
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            logger.error("Error while converting List<Long> to JSON: " + e.getMessage(), e);
            return "[]";
        }
    }
}
