package com.edunest.backend.modules.subject.entity;

import com.edunest.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subject_categories",
        uniqueConstraints = @UniqueConstraint(name = "uk_subject_category_code", columnNames = "code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectCategory extends BaseEntity {

    @Id
    @Column(length = 20, updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    private void validateId() {
        if (id == null || !id.matches("CAT\\d{5,}")) {
            throw new IllegalStateException("Subject category ID must be assigned as CAT#####");
        }
    }
}
