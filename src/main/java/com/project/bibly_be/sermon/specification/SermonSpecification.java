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

            // keyword 조건 (작성자 이름, 설교 제목에 대해 OR 조건)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                Predicate keywordPredicate = cb.or(
                        cb.like(cb.lower(root.get("owner").get("name")), pattern),
                        cb.like(cb.lower(root.get("sermonTitle")), pattern)
                        // 컨텐츠 검색 조건을 추가하려면, 별도의 Join과 조건이 필요합니다.
                );
                predicates.add(keywordPredicate);
            }

            // 모든 조건을 AND로 결합하여 반환
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
