package com.edunest.backend.modules.resource.entity;

import java.math.BigDecimal;




import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.common.enums.ResourceStatus;
import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.category.entity.Category;
import com.edunest.backend.modules.semester.entity.Semester;
import com.edunest.backend.modules.subject.entity.Subject;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.edunest.backend.modules.university.entity.University;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.year.entity.AcademicYear;

import com.edunest.backend.modules.college.entity.College;

@Entity
@Table(name = "resources", indexes = {
        @Index(name = "idx_resource_public", columnList = "active, published"),
        @Index(name = "idx_resource_branch_semester", columnList = "branch_id, semester_id"),
        @Index(name = "idx_resource_subject", columnList = "subject_id"),
        @Index(name = "idx_resource_material_access", columnList = "material_type, access_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Info
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean downloadable;
    
    @Column(nullable = false)
    private boolean watermarkEnabled;

    // Hierarchy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // Resource Type
    @Enumerated(EnumType.STRING)
    private MaterialType materialType;

    @Enumerated(EnumType.STRING)
    private AccessType accessType;

    @Enumerated(EnumType.STRING)
    private ResourceStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    private College college;

    // Pricing
    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal discountPrice;

    // File Storage (Cloudflare R2)
    @Column(nullable = false)
    private String fileKey;

    private String previewKey;

    private Long fileSizeBytes;

    private Integer pageCount;

    private Integer previewPages;

    // UI / Media
    private String thumbnailUrl;

    private String coverImageUrl;

    // Metadata
    private String version;

    private String language;

    @Column(columnDefinition = "TEXT")
    private String tags;

    // Status Flags
    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean published;

    // Analytics
    private Long downloadsCount;

    private Long salesCount;

    private Double ratingAverage;

    private Integer ratingCount;

    private Double popularityScore;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private LocalDateTime publishedAt;
}