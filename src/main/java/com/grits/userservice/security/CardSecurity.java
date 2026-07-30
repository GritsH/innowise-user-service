package com.grits.userservice.security;

import com.grits.userservice.dao.PaymentCardDao;
import com.grits.userservice.entity.PaymentCard;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardSecurity {

    private final PaymentCardDao cardDao;

    public void belongsToCurrentUser(UUID cardId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (SecurityHelper.isNotAuthenticated(authentication)) {
            throw new AccessDeniedException("Not authenticated");
        }

        if (SecurityHelper.isAdmin(authentication)) {
            return;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        UUID keycloakUserId = UUID.fromString(jwt.getSubject());
        PaymentCard card = cardDao.getPaymentCardById(cardId);

        if (!keycloakUserId.equals(card.getUser().getKeycloakUserId())) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
