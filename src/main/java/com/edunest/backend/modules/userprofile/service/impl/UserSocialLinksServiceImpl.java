package com.edunest.backend.modules.userprofile.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.modules.userprofile.dto.request.UserSocialLinksPatchRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserSocialLinksResponse;
import com.edunest.backend.modules.userprofile.entity.UserSocialLinks;
import com.edunest.backend.modules.userprofile.repository.UserSocialLinksRepository;
import com.edunest.backend.modules.userprofile.service.UserSocialLinksService;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class UserSocialLinksServiceImpl implements UserSocialLinksService {

    private final UserRepository userRepository;
    private final UserSocialLinksRepository socialLinksRepository;

    @Override
    @Transactional(readOnly = true)
    public UserSocialLinksResponse getCurrent() {
        Long userId = SecurityUtils.getCurrentUserId();

        return socialLinksRepository.findByUserId(userId)
                .map(this::map)
                .orElseGet(() -> emptyResponse());
    }

    @Override
    @Transactional
    public UserSocialLinksResponse patchCurrent(UserSocialLinksPatchRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserSocialLinks socialLinks = socialLinksRepository.findByUserId(userId)
                .orElseGet(() -> UserSocialLinks.builder()
                        .user(user)
                        .build());

        // Null means "do not change". A blank string explicitly clears the value.
        if (request.getLinkedinUrl() != null) {
            socialLinks.setLinkedinUrl(normalizeAndValidate(request.getLinkedinUrl(), "LinkedIn"));
        }

        if (request.getGithubUrl() != null) {
            socialLinks.setGithubUrl(normalizeAndValidate(request.getGithubUrl(), "GitHub"));
        }

        if (request.getPortfolioUrl() != null) {
            socialLinks.setPortfolioUrl(normalizeAndValidate(request.getPortfolioUrl(), "Portfolio"));
        }

        if (request.getLeetcodeUrl() != null) {
            socialLinks.setLeetcodeUrl(normalizeAndValidate(request.getLeetcodeUrl(), "LeetCode"));
        }

        return map(socialLinksRepository.save(socialLinks));
    }

    private String normalizeAndValidate(String value, String platform) {
        String normalized = value.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        try {
            URI uri = new URI(normalized);

            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (scheme == null || host == null
                    || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
                throw new BadRequestException(platform + " URL must be a valid HTTP or HTTPS URL");
            }

            return normalized;
        } catch (URISyntaxException ex) {
            throw new BadRequestException(platform + " URL is invalid");
        }
    }

    private UserSocialLinksResponse map(UserSocialLinks links) {
        return UserSocialLinksResponse.builder()
                .linkedinUrl(links.getLinkedinUrl())
                .githubUrl(links.getGithubUrl())
                .portfolioUrl(links.getPortfolioUrl())
                .leetcodeUrl(links.getLeetcodeUrl())
                .build();
    }

    private UserSocialLinksResponse emptyResponse() {
        return UserSocialLinksResponse.builder()
                .linkedinUrl(null)
                .githubUrl(null)
                .portfolioUrl(null)
                .leetcodeUrl(null)
                .build();
    }
}
