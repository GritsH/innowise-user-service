package com.grits.userservice.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends GlobalServiceException {

    public UserAlreadyExistsException(String email) {
        super("User with email " + email + " already exists", HttpStatus.CONFLICT);
    }
}
