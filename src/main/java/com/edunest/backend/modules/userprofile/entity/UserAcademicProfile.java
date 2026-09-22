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
@Table(name = "user_academic_profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_academic_profiles_user", columnNames = "user_id"),
        indexes = {
                @Index(name = "idx_user_academic_profiles_university", columnList = "university_id"),
                @Index(name = "idx_user_academic_profiles_college", columnList = "college_id"),
                @Index(name = "idx_user_academic_profiles_branch", columnList = "branch_id"),
                @Index(name = "idx_user_academic_profiles_academic_year", columnList = "academic_year_id"),
                @Index(name = "idx_user_academic_profiles_semester", columnList = "semester_id"),
                @Index(name = "idx_user_academic_profiles_active", columnList = "active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAcademicProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester currentSemester;

    @Column(name = "roll_number", length = 50)
    private String rollNumber;

    @Column(length = 20)
    private String division;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(precision = 4, scale = 2)
    private Double cgpa;

    @Column(name = "backlog_count")
    private Integer backlogCount;

    @Version
    @Column(name = "profile_version", nullable = false)
    private Integer profileVersion;

    @Column(nullable = false)
    private boolean active;

    public boolean isProfileCompleted() {
        return university != null && college != null && branch != null
                && academicYear != null && currentSemester != null
                && hasText(rollNumber) && hasText(division)
                && graduationYear != null && backlogCount != null;
    }

    public int getProfileCompletionPercentage() {
        int completed = 0;
        int total = 9;
        if (university != null) completed++;
        if (college != null) completed++;
        if (branch != null) completed++;
        if (academicYear != null) completed++;
        if (currentSemester != null) completed++;
        if (hasText(rollNumber)) completed++;
        if (hasText(division)) completed++;
        if (graduationYear != null) completed++;
        if (backlogCount != null) completed++;
        return Math.round((completed * 100.0f) / total);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}