package com.project.bibly_be.sermon.util;

import java.util.ArrayList;
import java.util.List;


public class ScriptureUtil {


    public static List<String> getScriptureMapping(String scripture) {
        if (scripture == null || scripture.trim().isEmpty() || "all".equalsIgnoreCase(scripture)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        // 원래 입력된 worshipType을 항상 포함
        result.add(scripture);

        if ("창세기".equalsIgnoreCase(scripture)) {
            result.add("창");
        } else if ("출애굽기".equalsIgnoreCase(scripture)) {
            result.add("출");
        } else if ("레위기".equalsIgnoreCase(scripture)) {
            result.add("레");
        } else if ("민수기".equalsIgnoreCase(scripture)) {
            result.add("민");
        } else if ("신명기".equalsIgnoreCase(scripture)) {
            result.add("신");
        } else if ("여호수아".equalsIgnoreCase(scripture)) {
            result.add("여호수아");
            result.add("여호");
        } else if ("사사기".equalsIgnoreCase(scripture)) {
            result.add("사사");
            result.add("사");
        } else if ("룻기".equalsIgnoreCase(scripture)) {
            result.add("룻");
        } else if ("사무엘상".equalsIgnoreCase(scripture)) {
            result.add("삼상");
            result.add("사상");
        } else if ("사무엘하".equalsIgnoreCase(scripture)) {
            result.add("삼하");
            result.add("사하");
        } else if ("열왕기상".equalsIgnoreCase(scripture)) {
            result.add("열상");
        } else if ("열왕기하".equalsIgnoreCase(scripture)) {
            result.add("열하");
        } else if ("역대상".equalsIgnoreCase(scripture)) {
            result.add("역상");
        } else if ("역대하".equalsIgnoreCase(scripture)) {
            result.add("역하");
        } else if ("에스라".equalsIgnoreCase(scripture)) {
            result.add("에스라");
            result.add("에");
        } else if ("느헤미야".equalsIgnoreCase(scripture)) {
            result.add("느헤미야");
            result.add("느");
        } else if ("에스더".equalsIgnoreCase(scripture)) {
            result.add("에스더");
            result.add("에");
        } else if ("욥기".equalsIgnoreCase(scripture)) {
            result.add("욥");
        } else if ("시편".equalsIgnoreCase(scripture)) {
            result.add("시");
        } else if ("잠언".equalsIgnoreCase(scripture)) {
            result.add("잠");
        } else if ("전도서".equalsIgnoreCase(scripture)) {
            result.add("전");
        } else if ("아가서".equalsIgnoreCase(scripture)) {
            result.add("아가");
        } else if ("이사야".equalsIgnoreCase(scripture)) {
            result.add("이사야");
            result.add("이사");
        } else if ("예레미야".equalsIgnoreCase(scripture)) {
            result.add("예레미야");
            result.add("예레");
        } else if ("예레미야애가".equalsIgnoreCase(scripture)) {
            result.add("예애");
            result.add("예레미야애가");
        } else if ("에스겔".equalsIgnoreCase(scripture)) {
            result.add("에스겔");
        } else if ("다니엘".equalsIgnoreCase(scripture)) {
            result.add("다니엘");
            result.add("다니");
        } else if ("호세아".equalsIgnoreCase(scripture)) {
            result.add("호세아");
            result.add("호세");
        } else if ("요엘".equalsIgnoreCase(scripture)) {
            result.add("요엘");
        } else if ("아모스".equalsIgnoreCase(scripture)) {
            result.add("아모스");
            result.add("아모");
        } else if ("오바댜".equalsIgnoreCase(scripture)) {
            result.add("오바댜");
            result.add("오바");
        } else if ("요나".equalsIgnoreCase(scripture)) {
            result.add("요나");
        } else if ("미가".equalsIgnoreCase(scripture)) {
            result.add("미가");
        } else if ("나훔".equalsIgnoreCase(scripture)) {
            result.add("나훔");
        } else if ("하박국".equalsIgnoreCase(scripture)) {
            result.add("하박국");
            result.add("하박");
        } else if ("스바냐".equalsIgnoreCase(scripture)) {
            result.add("스바냐");
            result.add("스바");
        } else if ("학개".equalsIgnoreCase(scripture)) {
            result.add("학개");
            result.add("학");
        } else if ("스가랴".equalsIgnoreCase(scripture)) {
            result.add("스가랴");
            result.add("스가");
        } else if ("말라기".equalsIgnoreCase(scripture)) {
            result.add("말라기");
            result.add("말");
        } else if ("마태복음".equalsIgnoreCase(scripture)) {
            result.add("마태복음");
            result.add("마태");
        } else if ("마가복음".equalsIgnoreCase(scripture)) {
            result.add("마가복음");
            result.add("마가");
        } else if ("누가복음".equalsIgnoreCase(scripture)) {
            result.add("누가복음");
            result.add("누가");
        } else if ("요한복음".equalsIgnoreCase(scripture)) {
            result.add("요한복음");
            result.add("요한");
        } else if ("사도행전".equalsIgnoreCase(scripture)) {
            result.add("사도행전");
            result.add("행");
        } else if ("로마서".equalsIgnoreCase(scripture)) {
            result.add("로마서");
            result.add("롬");
        } else if ("고린도전서".equalsIgnoreCase(scripture)) {
            result.add("고린도전서");
            result.add("고전");
        } else if ("고린도후서".equalsIgnoreCase(scripture)) {
            result.add("고린도후서");
            result.add("고후");
        } else if ("갈라디아서".equalsIgnoreCase(scripture)) {
            result.add("갈라디아서");
            result.add("갈");
        } else if ("에베소서".equalsIgnoreCase(scripture)) {
            result.add("에베소서");
            result.add("에베");
        } else if ("빌립보서".equalsIgnoreCase(scripture)) {
            result.add("빌립보서");
            result.add("빌");
        } else if ("골로새서".equalsIgnoreCase(scripture)) {
            result.add("골로새서");
            result.add("골");
        } else if ("데살로니가전서".equalsIgnoreCase(scripture)) {
            result.add("데살로니가전서");
            result.add("데전");
            result.add("전서");
        } else if ("데살로니가후서".equalsIgnoreCase(scripture)) {
            result.add("데살로니가후서");
            result.add("데후");
            result.add("후서");
        } else if ("디모데전서".equalsIgnoreCase(scripture)) {
            result.add("디모데전서");
            result.add("딤전");
        } else if ("디모데후서".equalsIgnoreCase(scripture)) {
            result.add("디모데후서");
            result.add("딤후");
        } else if ("디도서".equalsIgnoreCase(scripture)) {
            result.add("디도서");
            result.add("딤");
        } else if ("빌레몬서".equalsIgnoreCase(scripture)) {
            result.add("빌레몬서");
            result.add("빌모");
        } else if ("히브리서".equalsIgnoreCase(scripture)) {
            result.add("히브리서");
            result.add("히");
        } else if ("야고보서".equalsIgnoreCase(scripture)) {
            result.add("야고보서");
            result.add("약");
        } else if ("베드로전서".equalsIgnoreCase(scripture)) {
            result.add("베드로전서");
            result.add("벧전");
        } else if ("베드로후서".equalsIgnoreCase(scripture)) {
            result.add("베드로후서");
            result.add("벧후");
        } else if ("요한일서".equalsIgnoreCase(scripture)) {
            result.add("요한일서");
            result.add("요일");
        } else if ("요한이서".equalsIgnoreCase(scripture)) {
            result.add("요한이서");
            result.add("요이");
        } else if ("요한삼서".equalsIgnoreCase(scripture)) {
            result.add("요한삼서");
            result.add("요삼");
        } else if ("유다서".equalsIgnoreCase(scripture)) {
            result.add("유다서");
            result.add("유");
        } else if ("요한계시록".equalsIgnoreCase(scripture)) {
            result.add("요한계시록");
            result.add("계");
        }
        return result;
    }
}
