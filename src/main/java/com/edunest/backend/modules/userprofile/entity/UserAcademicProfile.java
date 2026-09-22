package com.edunest.backend.modules.userprofile.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.semester.entity.Semester;
import com.edunest.backend.modules.university.entity.University;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.year.entity.AcademicYear;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_academic_profiles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAcademicProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester currentSemester;

    private String rollNumber;

    private String division;

    private Integer graduationYear;

    private Double cgpa;

    private Integer backlogCount;

    @Column(nullable = false)
    private Integer profileVersion;

    @Column(nullable = false)
    private boolean active;
}