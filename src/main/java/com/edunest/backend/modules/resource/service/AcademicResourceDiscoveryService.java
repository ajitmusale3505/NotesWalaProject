package com.edunest.backend.modules.resource.service;

import com.edunest.backend.modules.resource.dto.response.AcademicResourceResponse;
import java.util.List;

public interface AcademicResourceDiscoveryService {
    List<AcademicResourceResponse> getCurrentUserResources(String subjectOfferingId);
}
