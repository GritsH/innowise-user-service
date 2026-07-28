package com.grits.userservice.security;

import com.grits.userservice.dao.UserDao;
import com.grits.userservice.entity.User;
import com.grits.userservice.exception.UserAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final UserDao userDao;

    public boolean isOwner(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UserAccessDeniedException();
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        UUID keycloakUserId = UUID.fromString(jwt.getSubject());
        User user = userDao.getUserById(userId);
        if (!keycloakUserId.equals(user.getKeycloakUserId())) {
            throw new UserAccessDeniedException();
        }
        return true;
    }
}
