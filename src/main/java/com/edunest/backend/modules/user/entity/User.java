package com.edunest.backend.modules.user.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.role.entity.Role;
import com.edunest.backend.modules.userprofile.enums.Gender;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
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

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String city;

    @Column(length = 200)
    private String address;

    @Column(name = "about_me", length = 500)
    private String aboutMe;

    @Column(name = "preferred_role", length = 100)
    private String preferredRole;

    @Column(name = "preferred_location", length = 200)
    private String preferredLocation;

    @Column(name = "employment_type", length = 100)
    private String employmentType;

    @Column(name = "availability", length = 150)
    private String availability;

    @ElementCollection
    @CollectionTable(name = "user_profile_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill", length = 100)
    @Builder.Default
    private Set<String> skills = new LinkedHashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_profile_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interest", length = 100)
    @Builder.Default
    private Set<String> interests = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
