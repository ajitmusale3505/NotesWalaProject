package com.edunest.backend.modules.elective.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.curriculum.entity.CurriculumSemester;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "elective_groups",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_elective_group_semester_code",
                columnNames = {"curriculum_semester_id", "code"}),
        indexes = {
                @Index(name = "idx_elective_group_semester_active",
                        columnList = "curriculum_semester_id, active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectiveGroup extends BaseEntity {

    @Id
    @Column(length = 20, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curriculum_semester_id", nullable = false)
    private CurriculumSemester curriculumSemester;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * Number of subjects a student must select from this group.
     * The database cannot know the member count at row-validation time,
     * therefore the service layer validates requiredSelections <= active members.
     */
    @Column(name = "required_selections", nullable = false)
    private Integer requiredSelections;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (id == null || !id.matches("ELG\\d{5,}")) {
            throw new IllegalStateException("Elective group ID must be assigned as ELG#####");
        }
        if (requiredSelections == null || requiredSelections < 1) {
            throw new IllegalStateException("Required selections must be positive");
        }
        if (displayOrder == null || displayOrder < 1) {
            throw new IllegalStateException("Display order must be positive");
        }
    }
}
