package com.edunest.backend.modules.userprofile.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSocialLinksPatchRequest {

    @Size(max = 500, message = "LinkedIn URL cannot exceed 500 characters")
    private String linkedinUrl;

    @Size(max = 500, message = "GitHub URL cannot exceed 500 characters")
    private String githubUrl;

    @Size(max = 500, message = "Portfolio URL cannot exceed 500 characters")
    private String portfolioUrl;

    @Size(max = 500, message = "LeetCode URL cannot exceed 500 characters")
    private String leetcodeUrl;
}
