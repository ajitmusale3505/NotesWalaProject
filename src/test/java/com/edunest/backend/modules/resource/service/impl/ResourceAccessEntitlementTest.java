package com.edunest.backend.modules.resource.service.impl;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.category.repository.CategoryRepository;
import com.edunest.backend.modules.college.repository.CollegeRepository;
import com.edunest.backend.modules.order.service.OrderService;
import com.edunest.backend.modules.resource.dto.response.ResourceAccessResponse;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceFileRepository;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.resourceentitlement.service.UserResourceEntitlementService;
import com.edunest.backend.modules.semester.repository.SemesterRepository;
import com.edunest.backend.modules.storage.service.StorageService;
import com.edunest.backend.modules.subscription.repository.SubscriptionRepository;
import com.edunest.backend.modules.subject.repository.SubjectRepository;
import com.edunest.backend.modules.university.repository.UniversityRepository;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.modules.year.repository.AcademicYearRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceAccessEntitlementTest {

    @Mock private ResourceRepository resourceRepository;
    @Mock private ResourceFileRepository resourceFileRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private BranchRepository branchRepository;
    @Mock private SemesterRepository semesterRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private OrderService orderService;
    @Mock private UserRepository userRepository;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private UserResourceEntitlementService userResourceEntitlementService;
    @Mock private StorageService storageService;
    @Mock private UniversityRepository universityRepository;
    @Mock private CollegeRepository collegeRepository;
    @Mock private AcademicYearRepository academicYearRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void freeResourceGrantsFullAccessWithoutEntitlement() {
        Resource resource = resource(AccessType.FREE);
        when(resourceRepository.findById(1L)).thenReturn(java.util.Optional.of(resource));
        when(storageService.generatePresignedUrl(anyString(), any())).thenReturn("signed-url");

        ResourceAccessResponse access = resourceService.checkAccess(10L, 1L);

        assertTrue(access.isAllowed());
        assertTrue(access.isCanViewFull());
        assertTrue(access.isCanDownload());
    }

    @Test
    void purchasedResourceRequiresPurchaseEntitlement() {
        Resource resource = resource(AccessType.DIRECT_PURCHASE);
        when(resourceRepository.findById(1L)).thenReturn(java.util.Optional.of(resource));
        when(userResourceEntitlementService.hasActiveEntitlement(10L, 1L, "PURCHASE"))
                .thenReturn(false);

        ResourceAccessResponse access = resourceService.checkAccess(10L, 1L);

        assertFalse(access.isAllowed());
        assertTrue(access.isCanPreview());
        assertFalse(access.isCanViewFull());
        assertFalse(access.isCanDownload());
        assertTrue(access.isRequiresPurchase());
    }

    @Test
    void purchasedResourceGrantsFullAccessOnlyWithEntitlement() {
        Resource resource = resource(AccessType.DIRECT_PURCHASE);
        when(resourceRepository.findById(1L)).thenReturn(java.util.Optional.of(resource));
        when(userResourceEntitlementService.hasActiveEntitlement(10L, 1L, "PURCHASE"))
                .thenReturn(true);
        when(storageService.generatePresignedUrl(anyString(), any())).thenReturn("signed-url");

        ResourceAccessResponse access = resourceService.checkAccess(10L, 1L);

        assertTrue(access.isAllowed());
        assertTrue(access.isCanViewFull());
    }

    @Test
    void subscriptionResourceUsesSubscriptionEntitlement() {
        Resource resource = resource(AccessType.SUBSCRIPTION);
        when(resourceRepository.findById(1L)).thenReturn(java.util.Optional.of(resource));
        when(userResourceEntitlementService.hasActiveEntitlement(10L, 1L, "SUBSCRIPTION"))
                .thenReturn(true);
        when(storageService.generatePresignedUrl(anyString(), any())).thenReturn("signed-url");

        ResourceAccessResponse access = resourceService.checkAccess(10L, 1L);

        assertTrue(access.isAllowed());
        assertTrue(access.isCanViewFull());
    }

    private Resource resource(AccessType accessType) {
        return Resource.builder()
                .id(1L)
                .title("Java Notes")
                .accessType(accessType)
                .fileKey("resources/main/test.pdf")
                .active(true)
                .published(true)
                .downloadable(true)
                .build();
    }
}
