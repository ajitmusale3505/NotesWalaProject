package com.edunest.backend.modules.subscription.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Plan Info
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Pricing
    @Column(nullable =  true)
    private BigDecimal monthlyPrice;

    private BigDecimal yearlyPrice;

    // Validity Rules
    private Integer validityDays;

    // Subject Selection Rules
    private Integer maxSelectableSubjects;

    private Integer maxSelectableSubjectsForFinalSemester;

    // Premium Academic Content Access
    private boolean solvedPyqAccess;

    private boolean premiumNotesAccess;

    private boolean labManualCodesAccess;

    private boolean vivaQuestionsAccess;

    private boolean bookPdfAccess;

    private boolean formulaSheetAccess;

    private boolean mcqBankAccess;

    private boolean vimpQuestionsAccess;

    private boolean mockTestAccess;

    // Premium Feature Access
    private boolean aiAccess;

    /** Maximum AI requests/credits granted for the subscription period. */
    private Integer aiCreditsPerPeriod;

    private boolean chatAccess;

    private boolean placementPackAccess;

    // Global Scope Rules
    private boolean allSubjectsAccess;

    private boolean allSemestersAccess;

    private boolean allBranchesAccess;

    // Status
    @Column(nullable = false)
    private boolean active;
}