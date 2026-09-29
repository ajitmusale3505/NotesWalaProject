package com.edunest.backend.modules.assessment.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.unit.entity.Unit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assessment_unit_mappings",
        uniqueConstraints = @UniqueConstraint(name = "uk_assessment_unit_mapping",
                columnNames = {"assessment_component_id", "unit_id"}),
        indexes = {
                @Index(name = "idx_assessment_mapping_component", columnList = "assessment_component_id, active"),
                @Index(name = "idx_assessment_mapping_unit", columnList = "unit_id, active")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentUnitMapping extends BaseEntity {
    @Id @Column(length = 20, updatable = false, nullable = false) private String id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "assessment_component_id", nullable = false) private AssessmentComponent assessmentComponent;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "unit_id", nullable = false) private Unit unit;
    @Column(name = "coverage_weight", nullable = false) private Integer coverageWeight;
    @Column(name = "display_order", nullable = false) private Integer displayOrder;
    @Column(nullable = false) private boolean active;

    @PrePersist @PreUpdate
    private void validate() {
        if (id == null || !id.matches("AUM\\d{5,}")) throw new IllegalStateException("Assessment mapping ID must be assigned as AUM#####");
        if (coverageWeight == null || coverageWeight < 0 || coverageWeight > 100) throw new IllegalStateException("Coverage weight must be between 0 and 100");
        if (displayOrder == null || displayOrder < 1) throw new IllegalStateException("Display order must be positive");
    }
}
