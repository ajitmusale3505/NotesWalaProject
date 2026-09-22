package com.edunest.backend.modules.resource.service;

import java.util.List;
import org.springframework.data.domain.Page;





import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.modules.resource.dto.request.CreateResourceRequest;
import com.edunest.backend.modules.resource.dto.response.ResourceAccessResponse;
import com.edunest.backend.modules.resource.dto.response.ResourceResponse;

import org.springframework.web.multipart.MultipartFile;
import com.edunest.backend.modules.resource.dto.request.AdminUploadResourceRequest;
import com.edunest.backend.modules.resource.dto.request.AdminPatchResourceRequest;

import com.edunest.backend.modules.resource.dto.request.AdminPutResourceRequest;

import java.io.InputStream;
import com.edunest.backend.modules.storage.dto.FileStreamResponse;
public interface ResourceService {

    ResourceResponse createResource(CreateResourceRequest request);

    List<ResourceResponse> getAllResources();

    ResourceResponse getResourceById(Long id);

    List<ResourceResponse> searchResources(String keyword);

    List<ResourceResponse> getResourcesByBranch(Long branchId);

    List<ResourceResponse> getResourcesBySemester(Long semesterId);

    List<ResourceResponse> getResourcesBySubject(Long subjectId);

    List<ResourceResponse> getResourcesByMaterialType(MaterialType materialType);

    List<ResourceResponse> getResourcesByAccessType(AccessType accessType);

    ResourceAccessResponse checkAccess(Long userId, Long resourceId);

    String generatePreviewUrl(Long resourceId);

    String generateDownloadUrl(Long userId, Long resourceId);

    String generateFullViewUrl(Long userId, Long resourceId);

    // PUBLIC APIs
    List<ResourceResponse> getPublicResources();
    Page<ResourceResponse> getPublicResourcesPage(int page, int size);
    Page<ResourceResponse> searchPublicResourcesPage(String keyword, int page, int size);

    Page<ResourceResponse> filterPublicResources(
            String keyword,
            Long categoryId,
            Long universityId,
            Long collegeId,
            Long branchId,
            Long academicYearId,
            Long semesterId,
            Long subjectId,
            MaterialType materialType,
            AccessType accessType,
            String language,
            Boolean freeOnly,
            Boolean discountedOnly,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            int page,
            int size,
            String sort,
            String direction);

    List<ResourceResponse> searchPublicResources(String keyword);

    List<ResourceResponse> getPublicResourcesByBranch(Long branchId);

    List<ResourceResponse> getPublicResourcesBySemester(Long semesterId);

    List<ResourceResponse> getPublicResourcesBySubject(Long subjectId);
    
    FileStreamResponse streamPreview(Long resourceId);

    FileStreamResponse streamView(Long userId, Long resourceId);

    FileStreamResponse streamDownload(Long userId, Long resourceId);
    
    List<ResourceResponse> filterResources(
            String keyword,
            Long branchId,
            Long semesterId,
            Long subjectId,
            MaterialType materialType
    );
    
    void trackDownload(Long resourceId);

    void trackView(Long resourceId);
    
    void deleteResource(Long resourceId);

    ResourceResponse publishResource(Long resourceId);

    ResourceResponse unpublishResource(Long resourceId);
    
    ResourceResponse adminUploadResource(
            MultipartFile pdfFile,
            MultipartFile previewFile,
            MultipartFile thumbnail,
            MultipartFile coverImage,
            AdminUploadResourceRequest request
    );
    
    ResourceResponse patchResource(
            Long resourceId,
            AdminPatchResourceRequest request
    );
    
    ResourceResponse replaceResource(
            Long resourceId,
            AdminPutResourceRequest request
    );
    
    
}