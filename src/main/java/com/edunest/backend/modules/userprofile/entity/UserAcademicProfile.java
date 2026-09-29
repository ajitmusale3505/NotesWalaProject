package com.edunest.backend.modules.userprofile.entity;

import java.math.BigDecimal;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.semester.entity.Semester;
import com.edunest.backend.modules.university.entity.University;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.userprofile.enums.Gender;
import com.edunest.backend.modules.year.entity.AcademicYear;
import com.edunest.backend.modules.exampattern.entity.ExamPattern;
import com.edunest.backend.modules.program.entity.Program;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_pattern_id")
    private ExamPattern examPattern;

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
    private BigDecimal cgpa;

    @Column(name = "backlog_count")
    private Integer backlogCount;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(name = "current_year")
    private Integer currentYear;

    @Column(length = 100)
    private String degree;

    @Column(length = 30)
    private String mode;

    @Column(name = "current_status", length = 50)
    private String currentStatus;

    @Column(name = "last_year_sgpa", precision = 5, scale = 2)
    private BigDecimal lastYearSgpa;

    @Column(name = "tenth_percentage", precision = 5, scale = 2)
    private BigDecimal tenthPercentage;

    @Column(name = "twelfth_percentage", precision = 5, scale = 2)
    private BigDecimal twelfthPercentage;

    @Column(name = "diploma_details", length = 100)
    private String diplomaDetails;

    @Column(name = "additional_information", length = 300)
    private String additionalInformation;

    @Column(name = "country", length = 50, nullable = false, columnDefinition = "varchar(50) default 'India'")
    @Builder.Default
    private String country = "India";

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "city", length = 100)
    private String city;

    @Version
    @Column(name = "profile_version", nullable = false)
    private Integer profileVersion;

    @Column(nullable = false)
    private boolean active;

    /**
     * The profile gate uses only the mandatory onboarding fields.
     * Roll number, division, CGPA and backlog count remain optional profile data.
     */
    public boolean isProfileCompleted() {
        return university != null && college != null && branch != null
                && academicYear != null && currentSemester != null
                && graduationYear != null
                && hasText(phoneNumber)
                && gender != null
                && currentYear != null
                && hasText(state)
                && hasText(city);
    }

    public int getProfileCompletionPercentage() {
        int completed = 0;
        int total = 10;

        if (university != null) completed++;
        if (college != null) completed++;
        if (branch != null) completed++;
        if (academicYear != null) completed++;
        if (currentSemester != null) completed++;
        if (hasText(phoneNumber)) completed++;
        if (gender != null) completed++;
        if (currentYear != null) completed++;
        if (graduationYear != null) completed++;
        if (hasText(state) && hasText(city)) completed++;

        return Math.round((completed * 100.0f) / total);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}