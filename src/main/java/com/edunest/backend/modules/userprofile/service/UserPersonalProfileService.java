package com.edunest.backend.modules.userprofile.service;

import com.edunest.backend.modules.userprofile.dto.request.UserPersonalProfileRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserPersonalProfileResponse;

public interface UserPersonalProfileService {
    UserPersonalProfileResponse getCurrent();
    UserPersonalProfileResponse updateCurrent(UserPersonalProfileRequest request);
}
