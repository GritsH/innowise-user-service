package com.grits.userservice.security;

import com.grits.userservice.dao.UserDao;
import com.grits.userservice.entity.User;
import com.grits.userservice.exception.UserNotFoundException;
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
public class UserAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final UserDao userDao;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        Authentication authentication = authenticationSupplier.get();
        if (SecurityHelper.isNotAuthenticated(authentication)) {
            return new AuthorizationDecision(false);
        }
        if (SecurityHelper.isAdmin(authentication)) {
            return new AuthorizationDecision(true);
        }

        String id = context.getVariables().get("id");
        if (id == null) {
            return new AuthorizationDecision(false);
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        UUID keycloakUserId = UUID.fromString(jwt.getSubject());
        try {
            User user = userDao.getUserById(UUID.fromString(id));
            return new AuthorizationDecision(keycloakUserId.equals(user.getKeycloakUserId()));
        } catch (UserNotFoundException e) {
            return new AuthorizationDecision(false);
        }
    }
}
