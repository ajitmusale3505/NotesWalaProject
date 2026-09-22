package com.edunest.backend.modules.collegebranch.entity;

import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.college.entity.College;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "college_branches",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"college_id", "branch_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollegeBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false)
    private boolean active;
}