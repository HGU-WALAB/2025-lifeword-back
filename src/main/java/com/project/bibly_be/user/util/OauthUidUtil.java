package com.project.bibly_be.user.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OauthUidUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    // 3칸짜리 배열 기본 형태 생성: [null, null, null]
    public static List<String> initUidList() {
        return Arrays.asList(null, null, null);
    }

    // DB JSON 문자열 -> List<String>(길이 3) 변환
    public static List<String> jsonToList(String json) {
        if (json == null || json.isEmpty()) {
            return initUidList();
        }
        try {
            List<String> list = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            if (list.size() != 3) {
                return initUidList();
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return initUidList();
        }
    }

    // List<String>(길이 3) -> JSON 배열 문자열
    public static String listToJson(List<String> list) {
        if (list == null || list.size() != 3) {
            list = initUidList();
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            e.printStackTrace();
            return "[null, null, null]";
        }
    }

}
