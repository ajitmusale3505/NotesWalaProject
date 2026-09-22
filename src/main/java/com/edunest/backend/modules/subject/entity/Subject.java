package com.edunest.backend.modules.subject.entity;

import com.edunest.backend.common.enums.ExamType;
import com.edunest.backend.common.enums.SubjectCategory;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.semester.entity.Semester;
import com.edunest.backend.modules.year.entity.AcademicYear;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Core
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private boolean active;

    // Classification
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubjectCategory subjectCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType examType;

    // Teaching Scheme
    private Integer lectureHours;
    private Integer tutorialHours;
    private Integer practicalHours;

    // Evaluation Scheme
    private Integer inSemMarks;
    private Integer endSemMarks;
    private Integer practicalMarks;
    private Integer oralMarks;
    private Integer termWorkMarks;

    // Credits
    private Integer credits;

    // Elective / Honors
    private boolean elective;
    private String electiveGroup;

    private boolean honors;
    private boolean minor;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;
}