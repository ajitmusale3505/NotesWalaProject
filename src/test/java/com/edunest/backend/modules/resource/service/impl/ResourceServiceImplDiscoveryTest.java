package com.edunest.backend.modules.resource.service.impl;

import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.resource.dto.response.ResourceResponse;
import com.edunest.backend.modules.storage.service.StorageService;
import com.edunest.backend.modules.category.repository.CategoryRepository;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.semester.repository.SemesterRepository;
import com.edunest.backend.modules.subject.repository.SubjectRepository;
import com.edunest.backend.modules.order.service.OrderService;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.modules.subscription.repository.SubscriptionRepository;
import com.edunest.backend.modules.resourceentitlement.service.ResourceEntitlementService;
import com.edunest.backend.modules.university.repository.UniversityRepository;
import com.edunest.backend.modules.college.repository.CollegeRepository;
import com.edunest.backend.modules.year.repository.AcademicYearRepository;
import com.edunest.backend.modules.resource.repository.ResourceFileRepository;
import com.edunest.backend.common.enums.MaterialType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplDiscoveryTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceFileRepository resourceFileRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private ResourceEntitlementService resourceEntitlementService;

    @Mock
    private StorageService storageService;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private CollegeRepository collegeRepository;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void searchUsesPublicSpecificationAndCapsPageSize() {
        when(resourceRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        resourceService.searchPublicResourcesPage("java", 2, 100);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(resourceRepository).findAll(any(Specification.class), pageable.capture());

        assertEquals(2, pageable.getValue().getPageNumber());
        assertEquals(50, pageable.getValue().getPageSize());
        assertEquals("createdAt", pageable.getValue().getSort().getOrderFor("createdAt").getProperty());
    }

    @Test
    void recentResourcesUseNewestFirst() {
        when(resourceRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        resourceService.getRecentPublicResourcesPage(0, 20);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(resourceRepository).findAll(any(Specification.class), pageable.capture());

        assertEquals("createdAt", pageable.getValue().getSort().iterator().next().getProperty());
        assertEquals(org.springframework.data.domain.Sort.Direction.DESC,
                pageable.getValue().getSort().iterator().next().getDirection());
    }

    @Test
    void popularResourcesUsePopularityAndDownloadSignals() {
        when(resourceRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        resourceService.getPopularPublicResourcesPage(0, 20);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(resourceRepository).findAll(any(Specification.class), pageable.capture());

        assertEquals("popularityScore",
                pageable.getValue().getSort().iterator().next().getProperty());
        assertEquals("downloadsCount",
                pageable.getValue().getSort().getOrderFor("downloadsCount").getProperty());
        assertEquals("ratingAverage",
                pageable.getValue().getSort().getOrderFor("ratingAverage").getProperty());
    }

    @Test
    void recommendedResourcesApplyAcademicContextAndPopularitySorting() {
        when(resourceRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        resourceService.getRecommendedPublicResourcesPage(
                1L, 2L, 3L, 4L, 5L, MaterialType.PREMIUM_NOTES, 1, 10);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(resourceRepository).findAll(any(Specification.class), pageable.capture());

        assertEquals(1, pageable.getValue().getPageNumber());
        assertEquals(10, pageable.getValue().getPageSize());
        assertEquals("popularityScore",
                pageable.getValue().getSort().iterator().next().getProperty());
    }
}
