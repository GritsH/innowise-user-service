package com.grits.userservice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UserNotFoundException extends GlobalServiceException{

    public UserNotFoundException(UUID user) {
        super("User " + user + " not found", HttpStatus.NOT_FOUND);
    }
}
