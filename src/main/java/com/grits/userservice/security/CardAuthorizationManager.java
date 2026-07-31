package com.grits.userservice.security;

import com.grits.userservice.dao.PaymentCardDao;
import com.grits.userservice.exception.PaymentCardNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CardAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final PaymentCardDao paymentCardDao;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        Authentication authentication = authenticationSupplier.get();
        if (SecurityHelper.isNotAuthenticated(authentication)) {
            return new AuthorizationDecision(false);
        }
        if (SecurityHelper.isAdmin(authentication)) {
            return new AuthorizationDecision(true);
        }

        String cardId = context.getVariables().get("id");
        if (cardId == null) {
            return new AuthorizationDecision(false);
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        UUID keycloakUserId = UUID.fromString(jwt.getSubject());
        try {
            UUID owner = paymentCardDao.findOwnerKeycloakId(UUID.fromString(cardId));
            return new AuthorizationDecision(keycloakUserId.equals(owner));
        } catch (PaymentCardNotFoundException e) {
            return new AuthorizationDecision(false);
        }
    }
}
