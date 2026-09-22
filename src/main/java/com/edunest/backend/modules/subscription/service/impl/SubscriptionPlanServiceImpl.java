package com.edunest.backend.modules.subscription.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.modules.subscription.dto.response.SubscriptionPlanResponse;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;
import com.edunest.backend.modules.subscription.repository.SubscriptionPlanRepository;
import com.edunest.backend.modules.subscription.service.SubscriptionPlanService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public List<SubscriptionPlanResponse> getAllPlans() {
        return subscriptionPlanRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlanResponse getPlanBySlug(String slug) {

        SubscriptionPlan plan = subscriptionPlanRepository
                .findBySlug(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subscription plan not found"));

        return mapToResponse(plan);
    }

    private SubscriptionPlanResponse mapToResponse(
            SubscriptionPlan plan) {

        return SubscriptionPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .slug(plan.getSlug())
                .description(plan.getDescription())
                .monthlyPrice(plan.getMonthlyPrice())
                .yearlyPrice(plan.getYearlyPrice())

                .validityDays(plan.getValidityDays())
                .maxSelectableSubjects(plan.getMaxSelectableSubjects())
                .maxSelectableSubjectsForFinalSemester(
                        plan.getMaxSelectableSubjectsForFinalSemester())

                .solvedPyqAccess(plan.isSolvedPyqAccess())
                .premiumNotesAccess(plan.isPremiumNotesAccess())
                .labManualCodesAccess(plan.isLabManualCodesAccess())
                .vivaQuestionsAccess(plan.isVivaQuestionsAccess())
                .bookPdfAccess(plan.isBookPdfAccess())
                .formulaSheetAccess(plan.isFormulaSheetAccess())
                .mcqBankAccess(plan.isMcqBankAccess())
                .vimpQuestionsAccess(plan.isVimpQuestionsAccess())
                .mockTestAccess(plan.isMockTestAccess())

                .aiAccess(plan.isAiAccess())
                .chatAccess(plan.isChatAccess())
                .placementPackAccess(plan.isPlacementPackAccess())

                .allSubjectsAccess(plan.isAllSubjectsAccess())
                .allSemestersAccess(plan.isAllSemestersAccess())
                .allBranchesAccess(plan.isAllBranchesAccess())

                .active(plan.isActive())
                .build();
    }
}