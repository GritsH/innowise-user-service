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

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String id = context.getVariables().get("id");
        if (id != null) {
            return validateById(jwt, id);
        }
        String email = context.getRequest().getParameter("email");
        if (email != null) {
            return validateByEmail(jwt, email);
        }
        return new AuthorizationDecision(false);
    }

    private AuthorizationDecision validateById(Jwt jwt, String id) {
        try {
            User user = userDao.getUserById(UUID.fromString(id));
            UUID keycloakUserId = UUID.fromString(jwt.getSubject());
            return new AuthorizationDecision(keycloakUserId.equals(user.getKeycloakUserId()));
        } catch (UserNotFoundException e) {
            return new AuthorizationDecision(false);
        }
    }

    private AuthorizationDecision validateByEmail(Jwt jwt, String email) {
        try {
            User user = userDao.getUserByEmail(email);
            UUID keycloakUserId = UUID.fromString(jwt.getSubject());
            return new AuthorizationDecision(keycloakUserId.equals(user.getKeycloakUserId()));
        } catch (UserNotFoundException e) {
            return new AuthorizationDecision(false);
        }
    }
}
