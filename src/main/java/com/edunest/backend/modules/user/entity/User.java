package com.edunest.backend.modules.user.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.role.entity.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users",
        indexes = @Index(name = "idx_users_role", columnList = "role_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
