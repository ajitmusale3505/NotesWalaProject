package com.edunest.backend.modules.userprofile.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSocialLinksResponse {

    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String leetcodeUrl;
}
