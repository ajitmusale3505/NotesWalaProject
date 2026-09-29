package com.edunest.backend.modules.curriculum.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.exampattern.entity.ExamPattern;
import com.edunest.backend.modules.program.entity.Program;
import com.edunest.backend.modules.university.entity.University;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curriculums",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_curriculum_branch_pattern_code",
                        columnNames = {"branch_id", "exam_pattern_id", "code"})
        },
        indexes = {
                @Index(name = "idx_curriculum_branch_pattern_active",
                        columnList = "branch_id, exam_pattern_id, active"),
                @Index(name = "idx_curriculum_university_active",
                        columnList = "university_id, active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curriculum extends BaseEntity {

    @Id
    @Column(length = 20, updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(length = 500)
    private String description;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    @Column(nullable = false)
    private boolean active;

    /*
     * Transitional FK references intentionally use the existing master-table
     * numeric PKs. Branch/University/ExamPattern/Program are migrated to
     * stored business IDs in their respective dependency phases. Keeping these
     * references compatible here prevents a partial PK rewrite from breaking
     * existing production data.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private com.edunest.backend.modules.branch.entity.Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_pattern_id", nullable = false)
    private ExamPattern examPattern;

    @PrePersist
    private void validateId() {
        if (id == null || !id.matches("CUR\\d{5,}")) {
            throw new IllegalStateException("Curriculum ID must be assigned as CUR#####");
        }
    }
}
