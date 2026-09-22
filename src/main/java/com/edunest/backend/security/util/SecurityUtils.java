package com.edunest.backend.security.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.edunest.backend.security.principal.CustomUserDetails;

/**
 * Centralized access to the authenticated application user.
 * Controllers must never trust a userId supplied by a client for "my data" operations.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    public static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails details)) {
            throw new AccessDeniedException("Authenticated user required");
        }
        return details;
    }

    public static Long getCurrentUserId() {
        return getCurrentUserDetails().getUser().getId();
    }

    public static boolean isAdmin() {
        return getCurrentUserDetails().getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    public static void requireSameUserOrAdmin(Long requestedUserId) {
        if (!isAdmin() && !getCurrentUserId().equals(requestedUserId)) {
            throw new AccessDeniedException("You are not allowed to access this user's data");
        }
    }
}
