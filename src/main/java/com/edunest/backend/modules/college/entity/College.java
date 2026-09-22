package com.edunest.backend.modules.college.entity;

import com.edunest.backend.modules.university.entity.University;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "colleges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(unique=true, nullable=false)
    private String code;

    private String city;

    private String state;

    @Column(nullable=false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;
}