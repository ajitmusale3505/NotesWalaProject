package com.edunest.backend.modules.userprofile.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.modules.userprofile.dto.request.UserPersonalProfileRequest;
import com.edunest.backend.modules.userprofile.dto.request.UserPersonalProfilePatchRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserPersonalProfileResponse;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import com.edunest.backend.modules.userprofile.service.UserPersonalProfileService;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPersonalProfileServiceImpl implements UserPersonalProfileService {

    private final UserRepository userRepository;
    private final UserAcademicProfileRepository academicProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public UserPersonalProfileResponse getCurrent() {
        Long userId = SecurityUtils.getCurrentUserId();
        return map(findUser(userId), academicProfileRepository.findByUserId(userId).orElse(null));
    }

    @Override
    @Transactional
    public UserPersonalProfileResponse updateCurrent(UserPersonalProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = findUser(userId);

        user.setFullName(request.getFullName().trim());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(normalize(request.getAddress()));

        UserAcademicProfile profile = academicProfileRepository.findByUserId(userId).orElse(null);
        if (profile != null) {
            profile.setPhoneNumber(request.getPhoneNumber().trim());
            profile.setGender(request.getGender());
            profile.setCountry("India");
            profile.setState(request.getState().trim());
            profile.setCity(request.getCity().trim());
            academicProfileRepository.save(profile);
        }

        return map(userRepository.save(user), profile);
    }

    @Override
    @Transactional
    public UserPersonalProfileResponse patchCurrent(UserPersonalProfilePatchRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = findUser(userId);
        UserAcademicProfile profile = academicProfileRepository.findByUserId(userId).orElse(null);

        if (hasText(request.getFullName())) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (hasText(request.getAddress())) {
            user.setAddress(request.getAddress().trim());
        }

        if (profile != null) {
            if (hasText(request.getPhoneNumber())) profile.setPhoneNumber(request.getPhoneNumber().trim());
            if (request.getGender() != null) profile.setGender(request.getGender());
            if (hasText(request.getState())) profile.setState(request.getState().trim());
            if (hasText(request.getCity())) profile.setCity(request.getCity().trim());
            profile.setCountry("India");
            academicProfileRepository.save(profile);
        }

        return map(userRepository.save(user), profile);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalize(String value) {
        if (value == null) return null;
        String v = value.trim();
        return v.isEmpty() ? null : v;
    }

    private UserPersonalProfileResponse map(User user, UserAcademicProfile profile) {
        return UserPersonalProfileResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(profile == null ? null : profile.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .gender(profile == null ? null : profile.getGender())
                .country("India")
                .state(profile == null ? null : profile.getState())
                .city(profile == null ? null : profile.getCity())
                .address(user.getAddress())
                .build();
    }
}
