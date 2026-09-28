package com.edunest.backend.modules.userprofile.service;

import com.edunest.backend.modules.userprofile.dto.request.UserSocialLinksPatchRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserSocialLinksResponse;

public interface UserSocialLinksService {

    UserSocialLinksResponse getCurrent();

    UserSocialLinksResponse patchCurrent(UserSocialLinksPatchRequest request);
}
