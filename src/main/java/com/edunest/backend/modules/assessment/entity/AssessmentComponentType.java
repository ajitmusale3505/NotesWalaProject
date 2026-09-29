package com.edunest.backend.modules.assessment.entity;

import com.edunest.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assessment_component_types",
        uniqueConstraints = @UniqueConstraint(name = "uk_assessment_type_code", columnNames = "code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentComponentType extends BaseEntity {

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
    private boolean practical;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    private void validateId() {
        if (id == null || !id.matches("ASM\\d{5,}")) {
            throw new IllegalStateException("Assessment component type ID must be assigned as ASM#####");
        }
    }
}
