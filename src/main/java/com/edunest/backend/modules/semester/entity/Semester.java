 package com.edunest.backend.modules.semester.entity;

import com.edunest.backend.modules.year.entity.AcademicYear;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "semesters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Integer number;

    @Column(nullable=false)
    private String name; // Semester 1, Semester 2

    @Column(nullable=false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;
}