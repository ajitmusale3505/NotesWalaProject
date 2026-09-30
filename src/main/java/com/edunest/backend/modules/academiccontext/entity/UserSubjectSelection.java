package com.edunest.backend.modules.academiccontext.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.subject.entity.SubjectOffering;
import com.edunest.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_subject_selections",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_subject_selection_user_offering",
                columnNames = {"user_id", "subject_offering_id"}),
        indexes = {
                @Index(name = "idx_user_subject_selection_user_active",
                        columnList = "user_id, active"),
                @Index(name = "idx_user_subject_selection_offering",
                        columnList = "subject_offering_id, active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubjectSelection extends BaseEntity {

    @Id
    @Column(length = 20, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_offering_id", nullable = false)
    private SubjectOffering subjectOffering;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    private void validateId() {
        if (id == null || !id.matches("USS\\d{5,}")) {
            throw new IllegalStateException("User subject selection ID must be assigned as USS#####");
        }
    }
}
