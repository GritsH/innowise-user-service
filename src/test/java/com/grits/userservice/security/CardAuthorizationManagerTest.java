package com.grits.userservice.security;

import com.grits.userservice.dao.PaymentCardDao;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardAuthorizationManagerTest {

    @Mock
    private PaymentCardDao paymentCardDao;

    @Mock
    private RequestAuthorizationContext context;

    @InjectMocks
    private CardAuthorizationManager authorizationManager;

    @Test
    @DisplayName("should deny when authentication is null")
    void denyWhenAuthenticationIsNull() {
        AuthorizationDecision decision = authorizationManager.check(() -> null, context);

        assertThat(decision.isGranted()).isFalse();

        verifyNoInteractions(paymentCardDao);
    }

    @Test
    @DisplayName("should grant access for admin")
    void grantAccessForAdmin() {
        Authentication authentication = authentication(UUID.randomUUID(), List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        AuthorizationDecision decision = authorizationManager.check(() -> authentication, context);

        assertThat(decision.isGranted()).isTrue();

        verifyNoInteractions(paymentCardDao);
    }

    @Test
    @DisplayName("should grant when user owns card")
    void grantWhenUserOwnsCard() {
        UUID cardId = UUID.randomUUID();
        UUID keycloakId = UUID.randomUUID();
        Authentication authentication = authentication(keycloakId, List.of());

        when(context.getVariables()).thenReturn(Map.of("id", cardId.toString()));
        when(paymentCardDao.findOwnerKeycloakId(cardId)).thenReturn(keycloakId);

        AuthorizationDecision decision = authorizationManager.check(() -> authentication, context);

        assertThat(decision.isGranted()).isTrue();

        verify(context).getVariables();
        verify(paymentCardDao).findOwnerKeycloakId(cardId);
        verifyNoMoreInteractions(paymentCardDao);
    }

    @Test
    @DisplayName("should deny when user does not own card")
    void denyWhenUserDoesNotOwnCard() {
        UUID cardId = UUID.randomUUID();
        UUID keycloakId = UUID.randomUUID();
        UUID anotherKeycloakId = UUID.randomUUID();
        Authentication authentication = authentication(keycloakId, List.of());

        when(context.getVariables()).thenReturn(Map.of("id", cardId.toString()));
        when(paymentCardDao.findOwnerKeycloakId(cardId)).thenReturn(anotherKeycloakId);

        AuthorizationDecision decision = authorizationManager.check(() -> authentication, context);

        assertThat(decision.isGranted()).isFalse();

        verify(context).getVariables();
        verify(paymentCardDao).findOwnerKeycloakId(cardId);
        verifyNoMoreInteractions(paymentCardDao);
    }

    private Authentication authentication(UUID keycloakId, Collection<GrantedAuthority> authorities) {
        Jwt jwt = Jwt.withTokenValue("dummy-token")
                .header("somealg", "tokentype")
                .subject(keycloakId.toString())
                .build();
        return new JwtAuthenticationToken(jwt, authorities);
    }
}