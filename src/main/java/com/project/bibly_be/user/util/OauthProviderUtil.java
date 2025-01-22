package com.project.bibly_be.user.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class OauthProviderUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 3칸짜리 배열 기본 형태 생성: [null, null, null]
    public static List<String> initProviderList() {
        return Arrays.asList(null, null, null);
    }

    // DB JSON 문자열 -> List<String>(길이 3) 변환
    public static List<String> jsonToList(String json) {
        if (json == null || json.isEmpty()) {
            return initProviderList();
        }
        try {
            List<String> list = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            if (list.size() != 3) {
                return initProviderList();
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return initProviderList();
        }
    }

    // List<String>(길이 3) -> JSON 배열 문자열
    public static String listToJson(List<String> list) {
        if (list == null || list.size() != 3) {
            list = initProviderList();
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            e.printStackTrace();
            return "[null, null, null]";
        }
    }

    // OAuth Provider명을 인덱스로 매핑
    public static int getProviderIndex(String provider) {
        switch (provider) {
            case "kakao":
                return 0;
            case "google":
                return 1;
            case "bibly":
                return 2;
            default:
                throw new IllegalArgumentException("Unknown provider: " + provider);
        }
    }
}
