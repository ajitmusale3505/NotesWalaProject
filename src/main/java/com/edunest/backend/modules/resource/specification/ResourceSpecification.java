package com.edunest.backend.modules.resource.specification;

import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.modules.resource.entity.Resource;
import org.springframework.data.jpa.domain.Specification;

public class ResourceSpecification {

    public static Specification<Resource> filter(
            String keyword,
            Long branchId,
            Long semesterId,
            Long subjectId,
            MaterialType materialType
    ) {

        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            predicate = cb.and(
                    predicate,
                    cb.isTrue(root.get("active"))
            );

            predicate = cb.and(
                    predicate,
                    cb.isTrue(root.get("published"))
            );

            if (keyword != null && !keyword.isBlank()) {

                String like = "%" + keyword.toLowerCase() + "%";

                predicate = cb.and(
                        predicate,
                        cb.or(
                                cb.like(cb.lower(root.get("title")), like),
                                cb.like(cb.lower(root.get("description")), like),
                                cb.like(cb.lower(root.get("tags")), like)
                        )
                );
            }

            if (branchId != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("branch").get("id"), branchId)
                );
            }

            if (semesterId != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("semester").get("id"), semesterId)
                );
            }

            if (subjectId != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("subject").get("id"), subjectId)
                );
            }

            if (materialType != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("materialType"), materialType)
                );
            }

            return predicate;
        };
    }
}