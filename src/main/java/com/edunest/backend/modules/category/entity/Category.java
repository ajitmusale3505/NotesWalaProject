package com.edunest.backend.modules.category.entity;

import java.time.LocalDateTime;

import com.edunest.backend.modules.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false,unique=true)
    private String name;

    @Column(nullable=false,unique=true)
    private String slug;

    private String description;

    private String icon;

    private Integer displayOrder;

    private boolean active;
    
    
}