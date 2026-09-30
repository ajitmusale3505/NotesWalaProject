package com.edunest.backend.modules.branch.entity;

import com.edunest.backend.modules.university.entity.University;
import com.edunest.backend.modules.year.entity.AcademicYear;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String code;

    @Column(nullable=false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    /**
     * A branch is a catalog/master entity. It is not tied to a student's
     * study-year academic-year record. Academic year is resolved from
     * semester/curriculum in the student's academic context.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id")
    private AcademicYear academicYear;
}
