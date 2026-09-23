package com.edunest.backend.modules.admin.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCmsOverviewResponse {
    private long users;
    private long admins;
    private long contributors;
    private long universities;
    private long colleges;
    private long branches;
    private long subjects;
    private long units;
    private long topics;
    private long resources;
    private long activeResources;
    private long publishedResources;
    private long orders;
    private long payments;
    private long subscriptions;
    private long subscriptionPlans;
    private long notifications;
    private long reviews;
    private long analyticsEvents;
    private boolean resourceManagement;
    private boolean curriculumManagement;
    private boolean notificationManagement;
    private boolean orderManagement;
    private boolean paymentManagement;
    private boolean subscriptionManagement;
    private boolean analyticsManagement;
    private boolean latexTemplateManagement;
    private boolean practicalManualManagement;
    private boolean practicalCodeManagement;
    private boolean chatModeration;
}