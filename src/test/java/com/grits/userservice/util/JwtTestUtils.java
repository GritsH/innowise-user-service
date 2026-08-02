package com.grits.userservice.util;

import com.grits.userservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;


@RequiredArgsConstructor
public class JwtTestUtils {

    public static RequestPostProcessor user(User user) {
        return jwt()
                .jwt(jwt -> jwt.subject(user.getKeycloakUserId().toString()))
                .authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public static RequestPostProcessor admin() {
        return jwt()
                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
