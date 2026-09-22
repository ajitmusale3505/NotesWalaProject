package com.edunest.backend.modules.resource.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
 

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.DocumentType;
import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.common.enums.ResourceStatus;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.category.entity.Category;
import com.edunest.backend.modules.category.repository.CategoryRepository;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.college.repository.CollegeRepository;
import com.edunest.backend.modules.resource.dto.request.CreateResourceRequest;
import com.edunest.backend.modules.resource.dto.response.ResourceResponse;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.resource.service.ResourceService;
import com.edunest.backend.modules.resource.specification.ResourceSpecification;
import com.edunest.backend.modules.semester.entity.Semester;
import com.edunest.backend.modules.semester.repository.SemesterRepository;
import com.edunest.backend.modules.storage.service.StorageService;
import com.edunest.backend.modules.subject.entity.Subject;
import com.edunest.backend.modules.subject.repository.SubjectRepository;

import com.edunest.backend.common.enums.SubscriptionStatus;
import com.edunest.backend.common.exception.AccessDeniedException;
import com.edunest.backend.modules.order.service.OrderService;
import com.edunest.backend.modules.resource.dto.response.ResourceAccessResponse;
import com.edunest.backend.modules.resourceentitlement.service.ResourceEntitlementService;
import com.edunest.backend.modules.subscription.entity.Subscription;
import com.edunest.backend.modules.subscription.repository.SubscriptionRepository;
import com.edunest.backend.modules.university.entity.University;
import com.edunest.backend.modules.university.repository.UniversityRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.modules.year.entity.AcademicYear;
import com.edunest.backend.modules.year.repository.AcademicYearRepository;

import lombok.RequiredArgsConstructor;
import com.edunest.backend.security.util.SecurityUtils;


import com.edunest.backend.modules.resource.dto.request.AdminUploadResourceRequest;

import org.springframework.web.multipart.MultipartFile;

import com.edunest.backend.modules.resource.dto.request.AdminPatchResourceRequest;

import com.edunest.backend.modules.resource.dto.request.AdminPutResourceRequest;
import com.edunest.backend.modules.storage.dto.FileStreamResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final CategoryRepository categoryRepository;
    private final BranchRepository branchRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ResourceEntitlementService resourceEntitlementService;
    private final StorageService storageService;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final AcademicYearRepository academicYearRepository;

    @Override
    @Transactional
    public ResourceResponse createResource(CreateResourceRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        validateAcademicHierarchy(branch.getUniversity(), null, branch, branch.getAcademicYear(), semester, subject);

        Resource resource = Resource.builder()
                .title(request.getTitle())
                .slug(uniqueSlug(generateSlug(request.getTitle())))
                .description(request.getDescription())
                .category(category)

                .university(branch.getUniversity())
                .academicYear(branch.getAcademicYear())

                .branch(branch)
                .semester(semester)
                .subject(subject)
                
                .documentType(request.getDocumentType())
                .materialType(request.getMaterialType())
                .accessType(request.getAccessType())
                .status(request.isPublished() ? ResourceStatus.APPROVED : ResourceStatus.DRAFT)
                .publishedAt(request.isPublished() ? java.time.LocalDateTime.now(java.time.ZoneOffset.UTC) : null)

                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .fileSizeBytes(request.getFileSizeBytes())
                .pageCount(request.getPageCount())
                .previewPages(request.getPreviewPages())
                .fileKey(request.getFileKey())
                .previewKey(request.getPreviewKey())

                .thumbnailUrl(request.getThumbnailUrl())
                .coverImageUrl(request.getCoverImageUrl())

                .version(request.getVersion())
                .language(request.getLanguage())
                .tags(normalizeTags(request.getTags()))
                .metadata(request.getMetadata())

                .active(request.isActive())
                .published(request.isPublished())

                .downloadsCount(0L)
                .salesCount(0L)
                .ratingAverage(0.0)
                .ratingCount(0)
                .popularityScore(0.0)
                .build();

        validatePricing(resource.getAccessType(), resource.getPrice(), resource.getDiscountPrice());
        normalizePublicationState(resource);
        Resource saved = resourceRepository.save(resource);

        return mapToResponse(saved);
    }
    
    
    @Override
    @Transactional
    public ResourceResponse patchResource(
            Long resourceId,
            AdminPatchResourceRequest request) {

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        if (request.getTitle() != null) {
            resource.setTitle(request.getTitle());
        }

        if (request.getSlug() != null) {
            resource.setSlug(request.getSlug());
        }

        if (request.getDescription() != null) {
            resource.setDescription(request.getDescription());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository
                    .findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Category not found"));
            resource.setCategory(category);
        }

        if (request.getUniversityId() != null) {
            University university = universityRepository
                    .findById(request.getUniversityId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("University not found"));
            resource.setUniversity(university);
        }

        if (request.getCollegeId() != null) {

            College college = collegeRepository
                    .findById(request.getCollegeId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("College not found"));

            resource.setCollege(college);
        }

        if (request.getBranchId() != null) {
            Branch branch = branchRepository
                    .findById(request.getBranchId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Branch not found"));
            resource.setBranch(branch);
        }

        if (request.getAcademicYearId() != null) {
            AcademicYear year = academicYearRepository
                    .findById(request.getAcademicYearId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Academic year not found"));
            resource.setAcademicYear(year);
        }

        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository
                    .findById(request.getSemesterId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Semester not found"));
            resource.setSemester(semester);
        }

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository
                    .findById(request.getSubjectId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Subject not found"));
            resource.setSubject(subject);
        }

        if (request.getDocumentType() != null) {
            resource.setDocumentType(request.getDocumentType());
        }
        if (request.getMaterialType() != null) {
            resource.setMaterialType(request.getMaterialType());
        }

        if (request.getAccessType() != null) {
            resource.setAccessType(request.getAccessType());
        }

        if (request.getPrice() != null) {
            resource.setPrice(request.getPrice());
        }

        if (request.getDiscountPrice() != null) {
            resource.setDiscountPrice(request.getDiscountPrice());
        }

        if (request.getPageCount() != null) {
            resource.setPageCount(request.getPageCount());
        }

        if (request.getPreviewPages() != null) {
            resource.setPreviewPages(request.getPreviewPages());
        }

        if (request.getVersion() != null) {
            resource.setVersion(request.getVersion());
        }

        if (request.getLanguage() != null) {
            resource.setLanguage(request.getLanguage());
        }

        if (request.getTags() != null) {
            resource.setTags(normalizeTags(request.getTags()));
        }
        if (request.getMetadata() != null) {
            resource.setMetadata(request.getMetadata().trim());
        }

        if (request.getDownloadable() != null) {
            resource.setDownloadable(request.getDownloadable());
        }

        if (request.getWatermarkEnabled() != null) {
            resource.setWatermarkEnabled(request.getWatermarkEnabled());
        }

        if (request.getActive() != null) {
            resource.setActive(request.getActive());
        }

        if (request.getPublished() != null) {
            resource.setPublished(request.getPublished());
        }

        Resource saved = resourceRepository.save(resource);

        return mapToResponse(saved);
    }
    
    @Override
    @Transactional
    public ResourceResponse adminUploadResource(
            MultipartFile pdfFile,
            MultipartFile previewFile,
            MultipartFile thumbnail,
            MultipartFile coverImage,
            AdminUploadResourceRequest request) {

        validateAdminUpload(request);

        // Validate all DB references before uploading anything to object storage.
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        University university = universityRepository.findById(request.getUniversityId())
                .orElseThrow(() -> new ResourceNotFoundException("University not found"));
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        College college = null;
        if (request.getCollegeId() != null) {
            college = collegeRepository.findById(request.getCollegeId())
                    .orElseThrow(() -> new ResourceNotFoundException("College not found"));
        }

        validateAcademicHierarchy(university, college, branch, academicYear, semester, subject);

        User uploader = userRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        String mainFileKey = null;
        String previewKey = null;
        String thumbnailUrl = null;
        String coverImageUrl = null;

        try {
            mainFileKey = storageService.uploadFile(pdfFile, "resources/main").getFileName();

            if (previewFile != null && !previewFile.isEmpty()) {
                previewKey = storageService.uploadFile(previewFile, "resources/preview").getFileName();
            }
            if (thumbnail != null && !thumbnail.isEmpty()) {
                thumbnailUrl = storageService.uploadFile(thumbnail, "resources/thumb").getFileName();
            }
            if (coverImage != null && !coverImage.isEmpty()) {
                coverImageUrl = storageService.uploadFile(coverImage, "resources/cover").getFileName();
            }

            Resource resource = Resource.builder()
                    .title(request.getTitle().trim())
                    .slug(uniqueSlug(request.getSlug() == null || request.getSlug().isBlank()
                            ? generateSlug(request.getTitle())
                            : request.getSlug().trim().toLowerCase(java.util.Locale.ROOT)))
                    .description(request.getDescription())
                    .category(category)
                    .university(university)
                    .branch(branch)
                    .academicYear(academicYear)
                    .college(college)
                    .semester(semester)
                    .subject(subject)
                    .documentType(request.getDocumentType())
                    .materialType(request.getMaterialType())
                    .accessType(request.getAccessType())
                    .price(request.getPrice())
                    .discountPrice(request.getDiscountPrice())
                    .fileKey(mainFileKey)
                    .previewKey(previewKey)
                    .thumbnailUrl(thumbnailUrl)
                    .coverImageUrl(coverImageUrl)
                    .pageCount(request.getPageCount())
                    .previewPages(request.getPreviewPages())
                    .version(request.getVersion())
                    .language(request.getLanguage())
                    .tags(normalizeTags(request.getTags()))
                    .metadata(request.getMetadata())
                    .downloadable(request.isDownloadable())
                    .watermarkEnabled(request.isWatermarkEnabled())
                    .active(request.isActive())
                    .published(request.isPublished())
                    .uploadedBy(uploader)
                    .downloadsCount(0L)
                    .salesCount(0L)
                    .ratingAverage(0.0)
                    .ratingCount(0)
                    .popularityScore(0.0)
                    .build();

            Resource saved = resourceRepository.save(resource);
            return mapToResponse(saved);
        } catch (RuntimeException ex) {
            // DB failure after a successful object upload must not leave orphaned files.
            deleteQuietly(mainFileKey);
            deleteQuietly(previewKey);
            deleteQuietly(thumbnailUrl);
            deleteQuietly(coverImageUrl);
            throw ex;
        }
    }

    private void deleteQuietly(String key) {
        if (key == null || key.isBlank()) return;
        try {
            storageService.deleteFile(key);
        } catch (Exception cleanupError) {
            org.slf4j.LoggerFactory.getLogger(ResourceServiceImpl.class)
                    .warn("Object-storage cleanup failed for uploaded key", cleanupError);
        }
    }

    @Override
    public List<ResourceResponse> getAllResources() {
        return resourceRepository.findAll(
                        PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ResourceResponse getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        return mapToResponse(resource);
    }

    @Override
    public List<ResourceResponse> searchResources(String keyword) {
        return resourceRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceResponse> getResourcesByBranch(Long branchId) {
        return resourceRepository.findByBranch_Id(branchId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceResponse> getResourcesBySemester(Long semesterId) {
        return resourceRepository.findBySemester_Id(semesterId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceResponse> getResourcesBySubject(Long subjectId) {
        return resourceRepository.findBySubject_Id(subjectId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceResponse> getResourcesByMaterialType(MaterialType materialType) {
        return resourceRepository.findByMaterialType(materialType)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceResponse> getResourcesByAccessType(AccessType accessType) {
        return resourceRepository.findByAccessType(accessType)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void trackDownload(Long resourceId) {
        if (resourceRepository.incrementDownloads(resourceId) != 1) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    @Override
    @Transactional
    public void trackView(Long resourceId) {
        if (resourceRepository.incrementViews(resourceId) != 1) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    @Override
    public ResourceAccessResponse checkAccess(
            Long userId,
            Long resourceId) {

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        AccessType accessType = resource.getAccessType();

        // ================= FREE =================
        if (accessType == AccessType.FREE) {
            return ResourceAccessResponse.builder()
                    .allowed(true)
                    .message("Free resource")
                    .canPreview(true)
                    .canViewFull(true)
                    .canDownload(resource.isDownloadable())
                    .requiresPurchase(false)
                    .viewUrl(generateSignedUrl(resource.getFileKey(), 30))
                    .downloadUrl(
                            resource.isDownloadable()
                                    ? generateSignedUrl(resource.getFileKey(), 10)
                                    : null
                    )
                    .build();
        }

        // ================= DIRECT PURCHASE =================
        if (accessType == AccessType.DIRECT_PURCHASE) {

            if (orderService.hasPurchased(userId, resourceId)) {
                return ResourceAccessResponse.builder()
                		.allowed(true)
                		.message("Purchased resource")
                		.canPreview(true)
                		.canViewFull(true)
                		.canDownload(resource.isDownloadable())
                		.requiresPurchase(false)
                		.viewUrl(generateSignedUrl(resource.getFileKey(), 30))
                		.downloadUrl(
                		        resource.isDownloadable()
                		                ? generateSignedUrl(resource.getFileKey(), 10)
                		                : null
                		)
                        .build();
            }

            return ResourceAccessResponse.builder()
            		.allowed(false)
            		.message("Purchase required")
            		.canPreview(true)
            		.canViewFull(false)
            		.canDownload(false)
            		.requiresPurchase(true)
            		.viewUrl(null)
            		.downloadUrl(null)
                    .build();
        }

        // ================= SUBSCRIPTION =================
        if (accessType == AccessType.SUBSCRIPTION) {

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found"));

            Subscription subscription = subscriptionRepository
                    .findByUserAndStatus(user, SubscriptionStatus.ACTIVE)
                    .orElse(null);

            if (subscription != null
                    && subscription.getEndDate() != null
                    && subscription.getEndDate().isAfter(java.time.LocalDateTime.now())) {

                boolean entitled =
                        resourceEntitlementService.hasEntitlement(
                                resourceId,
                                subscription.getPlan().getId());

                if (entitled) {
                    return ResourceAccessResponse.builder()
                    		.allowed(true)
                    		.message("Subscription access granted")
                    		.canPreview(true)
                    		.canViewFull(true)
                    		.canDownload(resource.isDownloadable())
                    		.requiresPurchase(false)
                    		.viewUrl(generateSignedUrl(resource.getFileKey(), 30))
                    		.downloadUrl(
                    		        resource.isDownloadable()
                    		                ? generateSignedUrl(resource.getFileKey(), 10)
                    		                : null
                    		)
                            .build();
                }
            }

            return ResourceAccessResponse.builder()
            		.allowed(false)
            		.message("Active subscription required")
            		.canPreview(true)
            		.canViewFull(false)
            		.canDownload(false)
            		.requiresPurchase(true)
            		.viewUrl(null)
            		.downloadUrl(null)
                    .build();
        }

        // ================= BUNDLE ONLY =================
        if (accessType == AccessType.BUNDLE_ONLY) {
            return ResourceAccessResponse.builder()
            		.allowed(false)
            		.message("Bundle access not implemented yet")
            		.canPreview(true)
            		.canViewFull(false)
            		.canDownload(false)
            		.requiresPurchase(true)
            		.viewUrl(null)
            		.downloadUrl(null)
                    .build();
        }

        return ResourceAccessResponse.builder()
                .allowed(false)
                .message("Access denied")
                .canPreview(false)
                .canViewFull(false)
                .canDownload(false)
                .requiresPurchase(false)
                .viewUrl(null)
                .downloadUrl(null)
                .build();
    }
    
    
    @Override
    public List<ResourceResponse> filterResources(
            String keyword,
            Long branchId,
            Long semesterId,
            Long subjectId,
            MaterialType materialType) {

        List<Resource> resources = resourceRepository.findAll(
                ResourceSpecification.filter(
                        keyword,
                        branchId,
                        semesterId,
                        subjectId,
                        materialType
                ),
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        return resources.stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    
    @Override
    public String generatePreviewUrl(Long resourceId) {

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        String previewKey = resource.getPreviewKey();

        if (previewKey == null || previewKey.isBlank()) {
            throw new BadRequestException("Preview not available");
        }

        return storageService.generatePublicUrl(previewKey);
    }
    
    
    @Override
    @Transactional
    public String generateDownloadUrl(
            Long userId,
            Long resourceId) {

        ResourceAccessResponse access =
                checkAccess(userId, resourceId);

        if (!access.isAllowed()) {
        	throw new AccessDeniedException("Access denied");
        }

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        if (!resource.isDownloadable()) {
            throw new BadRequestException(
                    "Download disabled by admin");
        }

        String fileKey = resource.getFileKey();

        return storageService.generatePublicUrl(fileKey);
    }
    
    @Override
    @Transactional
    public String generateFullViewUrl(Long userId, Long resourceId) {
        ResourceAccessResponse access = checkAccess(userId, resourceId);
        if (!access.isAllowed()) {
            throw new AccessDeniedException("Access denied");
        }

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        trackView(resourceId);
        return storageService.generatePublicUrl(resource.getFileKey());
    }

    @Override
    @Transactional
    public ResourceResponse publishResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        if (!resource.isActive()) {
            throw new BadRequestException("Inactive resource cannot be published");
        }
        if (resource.getFileKey() == null || resource.getFileKey().isBlank()) {
            throw new BadRequestException("Resource file is required before publishing");
        }
        resource.setPublished(true);
        resource.setStatus(ResourceStatus.APPROVED);
        resource.setPublishedAt(java.time.LocalDateTime.now(java.time.ZoneOffset.UTC));
        return mapToResponse(resourceRepository.save(resource));
    }

    @Override
    @Transactional
    public ResourceResponse unpublishResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        resource.setPublished(false);
        return mapToResponse(resourceRepository.save(resource));
    }

    @Override
    @Transactional
    public void deleteResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        deleteQuietlyOrThrow(resource.getFileKey());
        deleteQuietlyOrThrow(resource.getPreviewKey());
        deleteQuietlyOrThrow(resource.getThumbnailUrl());
        deleteQuietlyOrThrow(resource.getCoverImageUrl());

        resource.setActive(false);
        resource.setPublished(false);
        resource.setStatus(ResourceStatus.ARCHIVED);
        resourceRepository.save(resource);
    }

    private String normalizeTags(String tags) {
        if (tags == null || tags.isBlank()) return null;
        return java.util.Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .distinct()
                .limit(30)
                .collect(java.util.stream.Collectors.joining(","));
    }

    private void deleteQuietlyOrThrow(String key) {
        if (key == null || key.isBlank() || !key.startsWith("resources/")) return;
        storageService.deleteFile(key);
    }

    private String generateSignedUrl(String fileKey, int minutes) {
        return storageService.generatePresignedUrl(fileKey, java.time.Duration.ofMinutes(minutes));
    }

    @Override
    public FileStreamResponse streamPreview(Long resourceId) {

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        if (resource.getPreviewKey() == null || resource.getPreviewKey().isBlank()) {
            throw new BadRequestException("Preview not available");
        }

        return storageService.getFile(resource.getPreviewKey());
    }
    
    @Override
    @Transactional
    public FileStreamResponse streamView(
            Long userId,
            Long resourceId) {

        ResourceAccessResponse access =
                checkAccess(userId, resourceId);

        if (!access.isAllowed()) {
            throw new AccessDeniedException("Access denied");
        }

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        trackView(resourceId);

        return storageService.getFile(resource.getFileKey());
    }
    
    @Override
    @Transactional
    public FileStreamResponse streamDownload(
            Long userId,
            Long resourceId) {

        ResourceAccessResponse access =
                checkAccess(userId, resourceId);

        if (!access.isAllowed()) {
            throw new AccessDeniedException("Access denied");
        }

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        if (!resource.isDownloadable()) {
            throw new BadRequestException("Download disabled by admin");
        }

        trackDownload(resourceId);

        return storageService.getFile(resource.getFileKey());
    }
    
    @Override
    @Transactional
    public ResourceResponse replaceResource(
            Long resourceId,
            AdminPutResourceRequest request) {

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        University university = universityRepository
                .findById(request.getUniversityId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("University not found"));

        Branch branch = branchRepository
                .findById(request.getBranchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found"));

        AcademicYear academicYear = academicYearRepository
                .findById(request.getAcademicYearId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Academic year not found"));

        Semester semester = semesterRepository
                .findById(request.getSemesterId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Semester not found"));

        Subject subject = subjectRepository
                .findById(request.getSubjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject not found"));

        College college = null;

        if (request.getCollegeId() != null) {
            college = collegeRepository
                    .findById(request.getCollegeId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("College not found"));
        }

        resource.setTitle(request.getTitle());
        resource.setSlug(request.getSlug());
        resource.setDescription(request.getDescription());

        resource.setCategory(category);
        resource.setUniversity(university);
        resource.setCollege(college);
        resource.setBranch(branch);
        resource.setAcademicYear(academicYear);
        resource.setSemester(semester);
        resource.setSubject(subject);

        resource.setMaterialType(request.getMaterialType());
        resource.setAccessType(request.getAccessType());

        resource.setPrice(request.getPrice());
        resource.setDiscountPrice(request.getDiscountPrice());

        resource.setPageCount(request.getPageCount());
        resource.setPreviewPages(request.getPreviewPages());

        resource.setVersion(request.getVersion());
        resource.setLanguage(request.getLanguage());
        resource.setTags(request.getTags());

        resource.setDownloadable(request.isDownloadable());
        resource.setWatermarkEnabled(request.isWatermarkEnabled());
        resource.setActive(request.isActive());
        resource.setPublished(request.isPublished());

        validateAcademicHierarchy(resource.getUniversity(), resource.getCollege(), resource.getBranch(),
                resource.getAcademicYear(), resource.getSemester(), resource.getSubject());

        Resource saved = resourceRepository.save(resource);

        return mapToResponse(saved);
    }

    private void validateAdminUpload(AdminUploadResourceRequest request) {
        if (request == null) throw new IllegalArgumentException("Upload metadata is required");
        if (request.getTitle() == null || request.getTitle().isBlank() || request.getTitle().length() > 200) {
            throw new IllegalArgumentException("Title is required and must be at most 200 characters");
        }
        if (request.getDocumentType() == null || request.getAccessType() == null) {
            throw new IllegalArgumentException("Material type and access type are required");
        }
        if (request.getPrice() == null || request.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Price must be zero or positive");
        }
        if (request.getDiscountPrice() != null
                && (request.getDiscountPrice().signum() < 0
                || request.getDiscountPrice().compareTo(request.getPrice()) > 0)) {
            throw new IllegalArgumentException("Discount price must be non-negative and not exceed price");
        }
        if (request.getPageCount() != null && request.getPageCount() <= 0) {
            throw new IllegalArgumentException("Page count must be positive");
        }
        if (request.getPreviewPages() != null
                && (request.getPreviewPages() < 0
                || (request.getPageCount() != null && request.getPreviewPages() > request.getPageCount()))) {
            throw new IllegalArgumentException("Preview pages are invalid");
        }
    }

    private ResourceResponse mapToResponse(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .slug(resource.getSlug())
                .description(resource.getDescription())

                .categoryName(resource.getCategory() != null
                        ? resource.getCategory().getName()
                        : null)

                .branchName(resource.getBranch() != null
                        ? resource.getBranch().getName()
                        : null)
                .collegeName(
                	    resource.getCollege() != null
                	        ? resource.getCollege().getName()
                	        : null
                	)

                .semesterNumber(resource.getSemester() != null
                        ? resource.getSemester().getNumber()
                        : null)

                .subjectName(resource.getSubject() != null
                        ? resource.getSubject().getName()
                        : null)

                .documentType(resource.getDocumentType())
                .materialType(resource.getMaterialType())
                .status(resource.getStatus())
                .accessType(resource.getAccessType())

                .price(resource.getPrice())
                .discountPrice(resource.getDiscountPrice())

                .free(resource.getAccessType() == AccessType.FREE)
                .discounted(resource.getDiscountPrice() != null)

                .previewPages(resource.getPreviewPages())
                .pageCount(resource.getPageCount())
                .fileSizeBytes(resource.getFileSizeBytes())

                .previewUrl(
                        resource.getPreviewKey() == null
                                ? null
                                : storageService.generatePublicUrl(resource.getPreviewKey())
                )
                .signedPreviewUrl(
                	    resource.getPreviewKey() == null
                	        ? null
                	        : storageService.generatePublicUrl(resource.getPreviewKey())
                	)

                .thumbnailUrl(
                        storageService.generatePublicUrl(resource.getThumbnailUrl())
                )
                .coverImageUrl(
                        storageService.generatePublicUrl(resource.getCoverImageUrl())
                )
                .viewUrl("/api/resources/" + resource.getId() + "/view")

                .downloadUrl("/api/resources/" + resource.getId() + "/download")

                .version(resource.getVersion())
                .language(resource.getLanguage())
                .tags(resource.getTags())
                .metadata(resource.getMetadata())

                .purchased(false)
                .downloadable(resource.isDownloadable())
                .active(resource.isActive())
                .published(resource.isPublished())
                .publishedAt(resource.getPublishedAt())
                .downloadsCount(resource.getDownloadsCount())
                .ratingAverage(resource.getRatingAverage())
                .ratingCount(resource.getRatingCount())
                .popularityScore(resource.getPopularityScore())

                .uploadedByName(
                        resource.getUploadedBy() != null
                                ? resource.getUploadedBy().getFullName()
                                : null)

                .build();
    }
    private String uniqueSlug(String baseSlug) {
        String base = (baseSlug == null || baseSlug.isBlank()) ? "resource" : baseSlug;
        String slug = base;
        int suffix = 2;
        while (resourceRepository.existsBySlug(slug)) {
            slug = base + "-" + suffix++;
        }
        return slug;
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
    
    @Override
    public List<ResourceResponse> getPublicResources() {
        return resourceRepository
                .findByActiveTrueAndPublishedTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public org.springframework.data.domain.Page<ResourceResponse> getPublicResourcesPage(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        return resourceRepository.findByActiveTrueAndPublishedTrue(
                        org.springframework.data.domain.PageRequest.of(safePage, safeSize))
                .map(this::mapToResponse);
    }

    @Override
    public org.springframework.data.domain.Page<ResourceResponse> searchPublicResourcesPage(
            String keyword, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        String safeKeyword = keyword == null ? "" : keyword.trim();
        return resourceRepository.findByTitleContainingIgnoreCaseAndActiveTrueAndPublishedTrue(
                        safeKeyword, org.springframework.data.domain.PageRequest.of(safePage, safeSize))
                .map(this::mapToResponse);
    }

    @Override
    public org.springframework.data.domain.Page<ResourceResponse> filterPublicResources(
            String keyword, DocumentType documentType, Long categoryId, Long universityId, Long collegeId, Long branchId,
            Long academicYearId, Long semesterId, Long subjectId, MaterialType materialType,
            AccessType accessType, String language, Boolean freeOnly, Boolean discountedOnly,
            java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
            int page, int size, String sort, String direction) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        String property = switch (sort == null ? "newest" : sort.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "title" -> "title";
            case "price" -> "price";
            case "rating" -> "ratingAverage";
            case "downloads" -> "downloadsCount";
            case "popularity" -> "popularityScore";
            default -> "createdAt";
        };
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        return resourceRepository.findAll(
                ResourceSpecification.publicFilter(
                        keyword, documentType, categoryId, universityId, collegeId, branchId, academicYearId,
                        semesterId, subjectId, materialType, accessType, language, freeOnly,
                        discountedOnly, minPrice, maxPrice),
                PageRequest.of(safePage, safeSize, Sort.by(sortDirection, property)))
                .map(this::mapToResponse);
    }

    @Override
    public List<ResourceResponse> searchPublicResources(String keyword) {
        return resourceRepository
                .findByTitleContainingIgnoreCaseAndActiveTrueAndPublishedTrue(
                        keyword, PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ResourceResponse> getPublicResourcesByBranch(Long branchId) {
        return resourceRepository
                .findByBranch_IdAndActiveTrueAndPublishedTrue(branchId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ResourceResponse> getPublicResourcesBySemester(Long semesterId) {
        return resourceRepository
                .findBySemester_IdAndActiveTrueAndPublishedTrue(semesterId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ResourceResponse> getPublicResourcesBySubject(Long subjectId) {
        return resourceRepository
                .findBySubject_IdAndActiveTrueAndPublishedTrue(subjectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}