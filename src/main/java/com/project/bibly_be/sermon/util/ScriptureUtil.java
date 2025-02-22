package com.project.bibly_be.sermon.util;

import java.util.ArrayList;
import java.util.List;

public class ScriptureUtil {

    /**
     * 입력된 scripture 문자열에 대해 원본과, 만약 구절 범위가 있으면 그 범위를 확장한 후
     * 책 이름을 약어로 변환한 결과들을 모두 반환합니다.
     *
     * 예)
     * "창세기" → ["창세기", "창"]
     * "창세기 1:1" → ["창세기 1:1", "창 1:1", "창1:1"]
     * "창세기 1:1-13" → ["창세기 1:1-13", "창세기 1:1", "창세기 1:2", …, "창세기 1:13",
     *                     "창 1:1", "창 1:2", …, "창 1:13", "창1:1", "창1:2", …, "창1:13"]
     *
     * @param scripture 입력 문자열
     * @return 매핑된 문자열 리스트 (없으면 null)
     */
    public static List<String> getScriptureMapping(String scripture) {
        if (scripture == null || scripture.trim().isEmpty() || "all".equalsIgnoreCase(scripture)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        scripture = scripture.trim();
        // 원본 입력값 추가
        result.add(scripture);

        // 입력값을 공백 기준으로 최대 2개의 토큰(책 이름, 나머지 구절 정보)으로 분리
        String[] parts = scripture.split("\\s+", 2);
        String book = parts[0];
        String remainder = parts.length > 1 ? parts[1].trim() : "";

        // 만약 구절 정보(remainder)가 존재하고 "-"가 포함되어 있다면, 범위 확장
        if (!remainder.isEmpty() && remainder.contains("-")) {
            // 가정: remainder가 "chapter:start-end" 형식 (예: "1:1-13")
            String[] colonParts = remainder.split(":", 2);
            if (colonParts.length == 2) {
                String chapter = colonParts[0].trim();
                String rangePart = colonParts[1].trim();
                String[] rangeTokens = rangePart.split("[-~]");
                if (rangeTokens.length == 2) {
                    try {
                        int start = Integer.parseInt(rangeTokens[0].trim());
                        int end = Integer.parseInt(rangeTokens[1].trim());
                        // 원본 범위 문자열(예:"1:1-13")은 이미 result에 추가됨
                        // 확장: 각 개별 구절에 대해 두 가지 변형(공백 포함, 미포함)
                        for (int i = start; i <= end; i++) {
                            String verseWithSpace = chapter + ":" + i;       // 예: "1:1"
                            String verseNoSpace = chapter + ":" + i;           // 여기서는 두 경우가 동일할 수 있으므로,
                            // 필요 시 공백 제거 로직 추가 가능
                            // 책 이름에 대한 원본 형식와 약어 변환을 각각 추가
                            result.add(book + " " + verseWithSpace);
                            result.add(getAbbreviated(book) + " " + verseWithSpace);
                            result.add(book + verseNoSpace);
                            result.add(getAbbreviated(book) + verseNoSpace);
                        }
                    } catch (NumberFormatException e) {
                        // 구절 범위 파싱 실패 시, 그냥 일반 매핑 처리
                        addAbbreviation(result, book, remainder);
                    }
                } else {
                    // 범위 형식이 아닐 경우 일반 매핑 처리
                    addAbbreviation(result, book, remainder);
                }
            } else {
                addAbbreviation(result, book, remainder);
            }
        } else {
            // 구절 범위가 없으면 일반 매핑 처리
            addAbbreviation(result, book, remainder);
        }

        return result;
    }

    public static String getAbbreviated(String book) {
        if ("창세기".equalsIgnoreCase(book)) {
            return "창";
        } else if ("출애굽기".equalsIgnoreCase(book)) {
            return "출";
        } else if ("레위기".equalsIgnoreCase(book)) {
            return "레";
        } else if ("민수기".equalsIgnoreCase(book)) {
            return "민";
        } else if ("신명기".equalsIgnoreCase(book)) {
            return "신";
        } else if ("여호수아".equalsIgnoreCase(book)) {
            return "여호";
        } else if ("사사기".equalsIgnoreCase(book)) {
            return "사";
        } else if ("룻기".equalsIgnoreCase(book)) {
            return "룻";
        } else if ("사무엘상".equalsIgnoreCase(book)) {
            return "삼상";
        } else if ("사무엘하".equalsIgnoreCase(book)) {
            return "삼하";
        } else if ("열왕기상".equalsIgnoreCase(book)) {
            return "열상";
        } else if ("열왕기하".equalsIgnoreCase(book)) {
            return "열하";
        } else if ("역대상".equalsIgnoreCase(book)) {
            return "역상";
        } else if ("역대하".equalsIgnoreCase(book)) {
            return "역하";
        } else if ("에스라".equalsIgnoreCase(book)) {
            return "에";
        } else if ("느헤미야".equalsIgnoreCase(book)) {
            return "느";
        } else if ("에스더".equalsIgnoreCase(book)) {
            return "에";
        } else if ("욥기".equalsIgnoreCase(book)) {
            return "욥";
        } else if ("시편".equalsIgnoreCase(book)) {
            return "시";
        } else if ("잠언".equalsIgnoreCase(book)) {
            return "잠";
        } else if ("전도서".equalsIgnoreCase(book)) {
            return "전";
        } else if ("아가서".equalsIgnoreCase(book)) {
            return "아가";
        } else if ("이사야".equalsIgnoreCase(book)) {
            return "이사";
        } else if ("예레미야".equalsIgnoreCase(book)) {
            return "예레";
        } else if ("예레미야애가".equalsIgnoreCase(book)) {
            return "예애";
        } else if ("에스겔".equalsIgnoreCase(book)) {
            return "에스겔";
        } else if ("다니엘".equalsIgnoreCase(book)) {
            return "다니";
        } else if ("호세아".equalsIgnoreCase(book)) {
            return "호세";
        } else if ("요엘".equalsIgnoreCase(book)) {
            return "요엘";
        } else if ("아모스".equalsIgnoreCase(book)) {
            return "아모";
        } else if ("오바댜".equalsIgnoreCase(book)) {
            return "오바";
        } else if ("요나".equalsIgnoreCase(book)) {
            return "요나";
        } else if ("미가".equalsIgnoreCase(book)) {
            return "미가";
        } else if ("나훔".equalsIgnoreCase(book)) {
            return "나훔";
        } else if ("하박국".equalsIgnoreCase(book)) {
            return "하박";
        } else if ("스바냐".equalsIgnoreCase(book)) {
            return "스바";
        } else if ("학개".equalsIgnoreCase(book)) {
            return "학";
        } else if ("스가랴".equalsIgnoreCase(book)) {
            return "스가";
        } else if ("말라기".equalsIgnoreCase(book)) {
            return "말";
        } else if ("마태복음".equalsIgnoreCase(book)) {
            return "마태";
        } else if ("마가복음".equalsIgnoreCase(book)) {
            return "마가";
        } else if ("누가복음".equalsIgnoreCase(book)) {
            return "누가";
        } else if ("요한복음".equalsIgnoreCase(book)) {
            return "요한";
        } else if ("사도행전".equalsIgnoreCase(book)) {
            return "행";
        } else if ("로마서".equalsIgnoreCase(book)) {
            return "롬";
        } else if ("고린도전서".equalsIgnoreCase(book)) {
            return "고전";
        } else if ("고린도후서".equalsIgnoreCase(book)) {
            return "고후";
        } else if ("갈라디아서".equalsIgnoreCase(book)) {
            return "갈";
        } else if ("에베소서".equalsIgnoreCase(book)) {
            return "에베";
        } else if ("빌립보서".equalsIgnoreCase(book)) {
            return "빌";
        } else if ("골로새서".equalsIgnoreCase(book)) {
            return "골";
        } else if ("데살로니가전서".equalsIgnoreCase(book)) {
            return "데전";
        } else if ("데살로니가후서".equalsIgnoreCase(book)) {
            return "데후";
        } else if ("디모데전서".equalsIgnoreCase(book)) {
            return "딤전";
        } else if ("디모데후서".equalsIgnoreCase(book)) {
            return "딤후";
        } else if ("디도서".equalsIgnoreCase(book)) {
            return "딤";
        } else if ("빌레몬서".equalsIgnoreCase(book)) {
            return "빌모";
        } else if ("히브리서".equalsIgnoreCase(book)) {
            return "히";
        } else if ("야고보서".equalsIgnoreCase(book)) {
            return "약";
        } else if ("베드로전서".equalsIgnoreCase(book)) {
            return "벧전";
        } else if ("베드로후서".equalsIgnoreCase(book)) {
            return "벧후";
        } else if ("요한일서".equalsIgnoreCase(book)) {
            return "요일";
        } else if ("요한이서".equalsIgnoreCase(book)) {
            return "요이";
        } else if ("요한삼서".equalsIgnoreCase(book)) {
            return "요삼";
        } else if ("유다서".equalsIgnoreCase(book)) {
            return "유";
        } else if ("요한계시록".equalsIgnoreCase(book)) {
            return "계";
        }
        // 해당 책에 대한 매핑이 없다면, 원본을 그대로 반환
        return book;
    }


    /**
     * 주어진 책 이름과 나머지 문자열을 기반으로, if-else 체인 방식으로 약어 매핑을 추가합니다.
     * @param result 기존 결과 리스트 (여기에 추가)
     * @param book 책 이름
     * @param remainder 구절 정보 (있으면)
     */
    private static void addAbbreviation(List<String> result, String book, String remainder) {
        String withSpace = remainder.isEmpty() ? "" : " " + remainder;
        String noSpace = remainder; // 공백 없이 붙인 경우
        if ("창세기".equalsIgnoreCase(book)) {
            result.add("창" + withSpace);
            if (!remainder.isEmpty()) result.add("창" + noSpace);
        } else if ("출애굽기".equalsIgnoreCase(book)) {
            result.add("출" + withSpace);
            if (!remainder.isEmpty()) result.add("출" + noSpace);
        } else if ("레위기".equalsIgnoreCase(book)) {
            result.add("레" + withSpace);
            if (!remainder.isEmpty()) result.add("레" + noSpace);
        } else if ("민수기".equalsIgnoreCase(book)) {
            result.add("민" + withSpace);
            if (!remainder.isEmpty()) result.add("민" + noSpace);
        } else if ("신명기".equalsIgnoreCase(book)) {
            result.add("신" + withSpace);
            if (!remainder.isEmpty()) result.add("신" + noSpace);
        } else if ("여호수아".equalsIgnoreCase(book)) {
            result.add("여호수아" + withSpace);
            result.add("여호" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("여호수아" + noSpace);
                result.add("여호" + noSpace);
            }
        } else if ("사사기".equalsIgnoreCase(book)) {
            result.add("사사" + withSpace);
            result.add("사" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("사사" + noSpace);
                result.add("사" + noSpace);
            }
        } else if ("룻기".equalsIgnoreCase(book)) {
            result.add("룻" + withSpace);
            if (!remainder.isEmpty()) result.add("룻" + noSpace);
        } else if ("사무엘상".equalsIgnoreCase(book)) {
            result.add("삼상" + withSpace);
            result.add("사상" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("삼상" + noSpace);
                result.add("사상" + noSpace);
            }
        } else if ("사무엘하".equalsIgnoreCase(book)) {
            result.add("삼하" + withSpace);
            result.add("사하" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("삼하" + noSpace);
                result.add("사하" + noSpace);
            }
        } else if ("열왕기상".equalsIgnoreCase(book)) {
            result.add("열상" + withSpace);
            if (!remainder.isEmpty()) result.add("열상" + noSpace);
        } else if ("열왕기하".equalsIgnoreCase(book)) {
            result.add("열하" + withSpace);
            if (!remainder.isEmpty()) result.add("열하" + noSpace);
        } else if ("역대상".equalsIgnoreCase(book)) {
            result.add("역상" + withSpace);
            if (!remainder.isEmpty()) result.add("역상" + noSpace);
        } else if ("역대하".equalsIgnoreCase(book)) {
            result.add("역하" + withSpace);
            if (!remainder.isEmpty()) result.add("역하" + noSpace);
        } else if ("에스라".equalsIgnoreCase(book)) {
            result.add("에스라" + withSpace);
            result.add("에" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("에스라" + noSpace);
                result.add("에" + noSpace);
            }
        } else if ("느헤미야".equalsIgnoreCase(book)) {
            result.add("느헤미야" + withSpace);
            result.add("느" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("느헤미야" + noSpace);
                result.add("느" + noSpace);
            }
        } else if ("에스더".equalsIgnoreCase(book)) {
            result.add("에스더" + withSpace);
            result.add("에" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("에스더" + noSpace);
                result.add("에" + noSpace);
            }
        } else if ("욥기".equalsIgnoreCase(book)) {
            result.add("욥" + withSpace);
            if (!remainder.isEmpty()) result.add("욥" + noSpace);
        } else if ("시편".equalsIgnoreCase(book)) {
            result.add("시" + withSpace);
            if (!remainder.isEmpty()) result.add("시" + noSpace);
        } else if ("잠언".equalsIgnoreCase(book)) {
            result.add("잠" + withSpace);
            if (!remainder.isEmpty()) result.add("잠" + noSpace);
        } else if ("전도서".equalsIgnoreCase(book)) {
            result.add("전" + withSpace);
            if (!remainder.isEmpty()) result.add("전" + noSpace);
        } else if ("아가서".equalsIgnoreCase(book)) {
            result.add("아가" + withSpace);
            if (!remainder.isEmpty()) result.add("아가" + noSpace);
        } else if ("이사야".equalsIgnoreCase(book)) {
            result.add("이사야" + withSpace);
            result.add("이사" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("이사야" + noSpace);
                result.add("이사" + noSpace);
            }
        } else if ("예레미야".equalsIgnoreCase(book)) {
            result.add("예레미야" + withSpace);
            result.add("예레" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("예레미야" + noSpace);
                result.add("예레" + noSpace);
            }
        } else if ("예레미야애가".equalsIgnoreCase(book)) {
            result.add("예애" + withSpace);
            result.add("예레미야애가" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("예애" + noSpace);
                result.add("예레미야애가" + noSpace);
            }
        } else if ("에스겔".equalsIgnoreCase(book)) {
            result.add("에스겔" + withSpace);
            if (!remainder.isEmpty()) result.add("에스겔" + noSpace);
        } else if ("다니엘".equalsIgnoreCase(book)) {
            result.add("다니엘" + withSpace);
            result.add("다니" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("다니엘" + noSpace);
                result.add("다니" + noSpace);
            }
        } else if ("호세아".equalsIgnoreCase(book)) {
            result.add("호세아" + withSpace);
            result.add("호세" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("호세아" + noSpace);
                result.add("호세" + noSpace);
            }
        } else if ("요엘".equalsIgnoreCase(book)) {
            result.add("요엘" + withSpace);
            if (!remainder.isEmpty()) result.add("요엘" + noSpace);
        } else if ("아모스".equalsIgnoreCase(book)) {
            result.add("아모스" + withSpace);
            result.add("아모" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("아모스" + noSpace);
                result.add("아모" + noSpace);
            }
        } else if ("오바댜".equalsIgnoreCase(book)) {
            result.add("오바댜" + withSpace);
            result.add("오바" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("오바댜" + noSpace);
                result.add("오바" + noSpace);
            }
        } else if ("요나".equalsIgnoreCase(book)) {
            result.add("요나" + withSpace);
            if (!remainder.isEmpty()) result.add("요나" + noSpace);
        } else if ("미가".equalsIgnoreCase(book)) {
            result.add("미가" + withSpace);
            if (!remainder.isEmpty()) result.add("미가" + noSpace);
        } else if ("나훔".equalsIgnoreCase(book)) {
            result.add("나훔" + withSpace);
            if (!remainder.isEmpty()) result.add("나훔" + noSpace);
        } else if ("하박국".equalsIgnoreCase(book)) {
            result.add("하박국" + withSpace);
            result.add("하박" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("하박국" + noSpace);
                result.add("하박" + noSpace);
            }
        } else if ("스바냐".equalsIgnoreCase(book)) {
            result.add("스바냐" + withSpace);
            result.add("스바" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("스바냐" + noSpace);
                result.add("스바" + noSpace);
            }
        } else if ("학개".equalsIgnoreCase(book)) {
            result.add("학개" + withSpace);
            result.add("학" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("학개" + noSpace);
                result.add("학" + noSpace);
            }
        } else if ("스가랴".equalsIgnoreCase(book)) {
            result.add("스가랴" + withSpace);
            result.add("스가" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("스가랴" + noSpace);
                result.add("스가" + noSpace);
            }
        } else if ("말라기".equalsIgnoreCase(book)) {
            result.add("말라기" + withSpace);
            result.add("말" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("말라기" + noSpace);
                result.add("말" + noSpace);
            }
        } else if ("마태복음".equalsIgnoreCase(book)) {
            result.add("마태복음" + withSpace);
            result.add("마태" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("마태복음" + noSpace);
                result.add("마태" + noSpace);
            }
        } else if ("마가복음".equalsIgnoreCase(book)) {
            result.add("마가복음" + withSpace);
            result.add("마가" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("마가복음" + noSpace);
                result.add("마가" + noSpace);
            }
        } else if ("누가복음".equalsIgnoreCase(book)) {
            result.add("누가복음" + withSpace);
            result.add("누가" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("누가복음" + noSpace);
                result.add("누가" + noSpace);
            }
        } else if ("요한복음".equalsIgnoreCase(book)) {
            result.add("요한복음" + withSpace);
            result.add("요한" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("요한복음" + noSpace);
                result.add("요한" + noSpace);
            }
        } else if ("사도행전".equalsIgnoreCase(book)) {
            result.add("사도행전" + withSpace);
            result.add("행" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("사도행전" + noSpace);
                result.add("행" + noSpace);
            }
        } else if ("로마서".equalsIgnoreCase(book)) {
            result.add("로마서" + withSpace);
            result.add("롬" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("로마서" + noSpace);
                result.add("롬" + noSpace);
            }
        } else if ("고린도전서".equalsIgnoreCase(book)) {
            result.add("고린도전서" + withSpace);
            result.add("고전" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("고린도전서" + noSpace);
                result.add("고전" + noSpace);
            }
        } else if ("고린도후서".equalsIgnoreCase(book)) {
            result.add("고린도후서" + withSpace);
            result.add("고후" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("고린도후서" + noSpace);
                result.add("고후" + noSpace);
            }
        } else if ("갈라디아서".equalsIgnoreCase(book)) {
            result.add("갈라디아서" + withSpace);
            result.add("갈" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("갈라디아서" + noSpace);
                result.add("갈" + noSpace);
            }
        } else if ("에베소서".equalsIgnoreCase(book)) {
            result.add("에베소서" + withSpace);
            result.add("에베" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("에베소서" + noSpace);
                result.add("에베" + noSpace);
            }
        } else if ("빌립보서".equalsIgnoreCase(book)) {
            result.add("빌립보서" + withSpace);
            result.add("빌" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("빌립보서" + noSpace);
                result.add("빌" + noSpace);
            }
        } else if ("골로새서".equalsIgnoreCase(book)) {
            result.add("골로새서" + withSpace);
            result.add("골" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("골로새서" + noSpace);
                result.add("골" + noSpace);
            }
        } else if ("데살로니가전서".equalsIgnoreCase(book)) {
            result.add("데살로니가전서" + withSpace);
            result.add("데전" + withSpace);
            result.add("전서" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("데살로니가전서" + noSpace);
                result.add("데전" + noSpace);
                result.add("전서" + noSpace);
            }
        } else if ("데살로니가후서".equalsIgnoreCase(book)) {
            result.add("데살로니가후서" + withSpace);
            result.add("데후" + withSpace);
            result.add("후서" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("데살로니가후서" + noSpace);
                result.add("데후" + noSpace);
                result.add("후서" + noSpace);
            }
        } else if ("디모데전서".equalsIgnoreCase(book)) {
            result.add("디모데전서" + withSpace);
            result.add("딤전" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("디모데전서" + noSpace);
                result.add("딤전" + noSpace);
            }
        } else if ("디모데후서".equalsIgnoreCase(book)) {
            result.add("디모데후서" + withSpace);
            result.add("딤후" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("디모데후서" + noSpace);
                result.add("딤후" + noSpace);
            }
        } else if ("디도서".equalsIgnoreCase(book)) {
            result.add("디도서" + withSpace);
            result.add("딤" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("디도서" + noSpace);
                result.add("딤" + noSpace);
            }
        } else if ("빌레몬서".equalsIgnoreCase(book)) {
            result.add("빌레몬서" + withSpace);
            result.add("빌모" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("빌레몬서" + noSpace);
                result.add("빌모" + noSpace);
            }
        } else if ("히브리서".equalsIgnoreCase(book)) {
            result.add("히브리서" + withSpace);
            result.add("히" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("히브리서" + noSpace);
                result.add("히" + noSpace);
            }
        } else if ("야고보서".equalsIgnoreCase(book)) {
            result.add("야고보서" + withSpace);
            result.add("약" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("야고보서" + noSpace);
                result.add("약" + noSpace);
            }
        } else if ("베드로전서".equalsIgnoreCase(book)) {
            result.add("베드로전서" + withSpace);
            result.add("벧전" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("베드로전서" + noSpace);
                result.add("벧전" + noSpace);
            }
        } else if ("베드로후서".equalsIgnoreCase(book)) {
            result.add("베드로후서" + withSpace);
            result.add("벧후" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("베드로후서" + noSpace);
                result.add("벧후" + noSpace);
            }
        } else if ("요한일서".equalsIgnoreCase(book)) {
            result.add("요한일서" + withSpace);
            result.add("요일" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("요한일서" + noSpace);
                result.add("요일" + noSpace);
            }
        } else if ("요한이서".equalsIgnoreCase(book)) {
            result.add("요한이서" + withSpace);
            result.add("요이" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("요한이서" + noSpace);
                result.add("요이" + noSpace);
            }
        } else if ("요한삼서".equalsIgnoreCase(book)) {
            result.add("요한삼서" + withSpace);
            result.add("요삼" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("요한삼서" + noSpace);
                result.add("요삼" + noSpace);
            }
        } else if ("유다서".equalsIgnoreCase(book)) {
            result.add("유다서" + withSpace);
            result.add("유" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("유다서" + noSpace);
                result.add("유" + noSpace);
            }
        } else if ("요한계시록".equalsIgnoreCase(book)) {
            result.add("요한계시록" + withSpace);
            result.add("계" + withSpace);
            if (!remainder.isEmpty()) {
                result.add("요한계시록" + noSpace);
                result.add("계" + noSpace);
            }
        }
        //return result;
    }
}
