package com.project.bibly_be.sermon.specification;

import com.project.bibly_be.sermon.entity.Sermon;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SermonSpecification {

    public static Specification<Sermon> withFilters(
            UUID userId,
            String keyword,
            List<String> worships,
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<String> scriptures,
            int mode
            ) {

        return (Root<Sermon> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            // 조건들을 저장할 List 생성
            List<Predicate> predicates = new ArrayList<>();

            // 모드에 따른 조건 추가
            if (mode == 0) {
                // 모드 0: 공개된 설교만
                predicates.add(cb.isTrue(root.get("isPublic")));
            } else if (mode == 1) {
                // 모드 1: 해당 사용자의 설교만
                predicates.add(cb.equal(root.get("owner").get("id"), userId));
            } else if (mode == 2) {
                // 모드 2: 공개된 설교이면서 동시에 해당 사용자의 설교
                predicates.add(cb.and(
                        cb.isTrue(root.get("isPublic")),
                        cb.equal(root.get("owner").get("id"), userId)
                ));
            } else if (mode == 3) {
                // 모드 3: 비공개이면서 해당 사용자의 설교
                predicates.add(cb.and(
                        cb.isFalse(root.get("isPublic")),
                        cb.equal(root.get("owner").get("id"), userId)
                ));
            }else if (mode == 4) {
                // 모드 4: 모든 사용자의 설교
                predicates.add(cb.conjunction());
            }

            // worshipTypes 조건: List가 비어있지 않으면 IN 조건 사용
            if (worships != null && !worships.isEmpty()) {
                predicates.add(root.get("worshipType").in(worships));
            }

            // 날짜 조건
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("sermonDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("sermonDate"), endDate));
            }

            // scripture 키워드 조건: scriptureKeywords 리스트의 각 요소에 대해 OR 조건 결합
            if (scriptures != null && !scriptures.isEmpty()) {
                List<Predicate> scripturePredicates = new ArrayList<>();
                for (String sk : scriptures) {
                    if (sk != null && !sk.trim().isEmpty()) {
                        String pattern = "%" + sk.toLowerCase() + "%";
                        Predicate mainPredicate = cb.like(cb.lower(root.get("mainScripture")), pattern);
                        Predicate additionalPredicate = cb.like(cb.lower(root.get("additionalScripture")), pattern);
                        scripturePredicates.add(mainPredicate);
                        scripturePredicates.add(additionalPredicate);
                    }
                }
                // 여러 scripture 조건을 OR로 결합
                if (!scripturePredicates.isEmpty()) {
                    predicates.add(cb.or(scripturePredicates.toArray(new Predicate[0])));
                }
            }

            // keyword 조건: 작성자 이름, 설교 제목, 설교 요약, 컨텐츠 내용에 대해 OR 조건
            // 컨텐츠 내용을 검색하기 위해 LEFT JOIN 추가
            Join<Sermon, ?> contentJoin = root.join("contents", JoinType.LEFT);

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                Predicate keywordPredicate = cb.or(
                        cb.like(cb.lower(root.get("owner").get("name")), pattern),
                        cb.like(cb.lower(root.get("sermonTitle")), pattern),
                        cb.like(cb.lower(root.get("summary")), pattern),
                        cb.like(cb.lower(contentJoin.get("contentText")), pattern)
                );
                predicates.add(keywordPredicate);

                // 추가: 정렬 우선순위 CASE 표현식을 ORDER BY에 추가하려면,
                // CriteriaQuery의 orderBy()를 직접 설정합니다.
                // 단, count 쿼리 시에는 orderBy()가 무시되어야 하므로,
                // query.getResultType()이 Sermon가 아닐 경우(orderBy 적용 X) 처리합니다.
                if (query.getResultType() != Long.class) {
                    Expression<Object> orderPriority = cb.selectCase()
                            .when(cb.equal(cb.lower(root.get("owner").get("name")), keyword.toLowerCase()), 1)
                            .when(cb.equal(cb.lower(root.get("sermonTitle")), keyword.toLowerCase()), 2)
                            .when(cb.equal(cb.lower(root.get("summary")), keyword.toLowerCase()), 3)
                            .when(cb.equal(cb.lower(contentJoin.get("contentText")), keyword.toLowerCase()), 4)
                            .otherwise(5);
                    // 오름차순 정렬하면 우선순위가 낮은 값이 먼저 나옵니다.
                    query.orderBy(cb.asc(orderPriority));
                }
            }

            // 모든 조건을 AND로 결합하여 반환
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
