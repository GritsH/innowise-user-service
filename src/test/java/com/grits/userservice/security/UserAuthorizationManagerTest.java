package com.grits.userservice.security;

import com.grits.userservice.dao.UserDao;
import com.grits.userservice.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthorizationManagerTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RequestAuthorizationContext context;

    @InjectMocks
    private UserAuthorizationManager authorizationManager;

    @Test
    @DisplayName("should deny when authentication is null")
    void denyWhenAuthenticationIsNull() {
        AuthorizationDecision decision = authorizationManager.check(() -> null, context);

        assertThat(decision.isGranted()).isFalse();

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("should grant access for admin")
    void grantAccessForAdmin() {
        Authentication authentication = authenticatedUser(UUID.randomUUID(), List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        AuthorizationDecision decision = authorizationManager.check(() -> authentication, context);

        assertThat(decision.isGranted()).isTrue();

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("should grant access when authenticated user owns resource")
    void grantUserOwnsResource() {
        UUID userId = UUID.randomUUID();
        UUID keycloakUserId = UUID.randomUUID();
        User user = mock(User.class);
        Authentication authentication = authenticatedUser(keycloakUserId, List.of());

        when(context.getVariables()).thenReturn(Map.of("id", userId.toString()));
        when(userDao.getUserById(userId)).thenReturn(user);
        when(user.getKeycloakUserId()).thenReturn(keycloakUserId);

        AuthorizationDecision decision = authorizationManager.check(() -> authentication, context);

        assertThat(decision.isGranted()).isTrue();

        verify(context).getVariables();
        verify(userDao).getUserById(userId);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    @DisplayName("should deny access when user does not own resource")
    void denyWhenUserDoesNotOwnResource() {
        UUID userId = UUID.randomUUID();
        UUID keycloakId = UUID.randomUUID();
        UUID anotherKeycloakId = UUID.randomUUID();
        Authentication authentication = authenticatedUser(keycloakId, List.of());
        User user = mock(User.class);

        when(context.getVariables()).thenReturn(Map.of("id", userId.toString()));
        when(userDao.getUserById(userId)).thenReturn(user);
        when(user.getKeycloakUserId()).thenReturn(anotherKeycloakId);

        AuthorizationDecision decision = authorizationManager.check(() -> authentication, context);

        assertThat(decision.isGranted()).isFalse();

        verify(context).getVariables();
        verify(userDao).getUserById(userId);
        verifyNoMoreInteractions(userDao);
    }

    private Authentication authenticatedUser(UUID keycloakId, Collection<GrantedAuthority> authorities) {
        Jwt jwt = Jwt.withTokenValue("dummy-token")
                .header("somealg", "tokentype")
                .subject(keycloakId.toString())
                .build();
        return new JwtAuthenticationToken(jwt, authorities);
    }
}