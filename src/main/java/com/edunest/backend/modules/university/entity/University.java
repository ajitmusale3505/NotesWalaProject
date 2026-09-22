package com.edunest.backend.modules.university.entity;

import com.edunest.backend.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "universities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class University extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "short_code", nullable = false, unique = true)
    private String shortCode;

    private String city;

    private String state;

    private String country;

    @Column(nullable = false)
    private boolean active;
}