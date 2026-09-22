package com.edunest.backend.modules.userprofile.service;

import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfileRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserAcademicProfileResponse;
import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfilePatchRequest;

public interface UserAcademicProfileService {

    UserAcademicProfileResponse getByUserId(Long userId);

    UserAcademicProfileResponse saveProfile(
            UserAcademicProfileRequest request);

    UserAcademicProfileResponse updateProfile(
            Long userId,
            UserAcademicProfileRequest request);
    
    UserAcademicProfileResponse patchProfile(
            Long userId,
            UserAcademicProfilePatchRequest request);
}