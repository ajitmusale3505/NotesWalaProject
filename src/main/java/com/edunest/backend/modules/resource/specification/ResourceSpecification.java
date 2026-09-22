package com.edunest.backend.modules.resource.specification;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.modules.resource.entity.Resource;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;

public final class ResourceSpecification {

    private ResourceSpecification() {
    }

    public static Specification<Resource> publicFilter(
            String keyword,
            Long categoryId,
            Long universityId,
            Long collegeId,
            Long branchId,
            Long academicYearId,
            Long semesterId,
            Long subjectId,
            MaterialType materialType,
            AccessType accessType,
            String language,
            Boolean freeOnly,
            Boolean discountedOnly,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        return (root, query, cb) -> {
            var predicate = cb.and(
                    cb.isTrue(root.get("active")),
                    cb.isTrue(root.get("published"))
            );

            if (hasText(keyword)) {
                String like = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.get("tags")), like),
                        cb.like(cb.lower(root.get("language")), like)
                ));
            }
            if (categoryId != null) predicate = cb.and(predicate, cb.equal(root.get("category").get("id"), categoryId));
            if (universityId != null) predicate = cb.and(predicate, cb.equal(root.get("university").get("id"), universityId));
            if (collegeId != null) predicate = cb.and(predicate, cb.equal(root.get("college").get("id"), collegeId));
            if (branchId != null) predicate = cb.and(predicate, cb.equal(root.get("branch").get("id"), branchId));
            if (academicYearId != null) predicate = cb.and(predicate, cb.equal(root.get("academicYear").get("id"), academicYearId));
            if (semesterId != null) predicate = cb.and(predicate, cb.equal(root.get("semester").get("id"), semesterId));
            if (subjectId != null) predicate = cb.and(predicate, cb.equal(root.get("subject").get("id"), subjectId));
            if (materialType != null) predicate = cb.and(predicate, cb.equal(root.get("materialType"), materialType));
            if (accessType != null) predicate = cb.and(predicate, cb.equal(root.get("accessType"), accessType));
            if (hasText(language)) predicate = cb.and(predicate, cb.equal(cb.lower(root.get("language")), language.trim().toLowerCase(Locale.ROOT)));
            if (Boolean.TRUE.equals(freeOnly)) predicate = cb.and(predicate, cb.equal(root.get("accessType"), AccessType.FREE));
            if (Boolean.TRUE.equals(discountedOnly)) predicate = cb.and(predicate,
                    cb.and(cb.isNotNull(root.get("discountPrice")), cb.lessThan(root.get("discountPrice"), root.get("price"))));
            if (minPrice != null) predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            if (maxPrice != null) predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), maxPrice));

            return predicate;
        };
    }

    public static Specification<Resource> filter(
            String keyword, Long branchId, Long semesterId, Long subjectId, MaterialType materialType) {
        return publicFilter(keyword, null, null, null, branchId, null, semesterId, subjectId,
                materialType, null, null, null, null, null, null);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}