package com.edunest.backend.modules.year.entity;

import com.edunest.backend.modules.university.entity.University;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "academic_years")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String name;   // BE 2024 Pattern

    @Column(nullable=false, unique=true)
    private String code;   // SPPU-2024

    private Integer startYear;

    private Integer endYear;

    @Column(nullable=false)
    private boolean active;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;
}