package com.edunest.backend.modules.subscription.dto.response;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;

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
    private boolean chatAccess;
    private boolean placementPackAccess;

    // Global Scope Rules
    private boolean allSubjectsAccess;
    private boolean allSemestersAccess;
    private boolean allBranchesAccess;

    private boolean active;
}