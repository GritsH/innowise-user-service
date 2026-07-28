package com.grits.userservice.exception;

import org.springframework.http.HttpStatus;

public class UserAccessDeniedException extends GlobalServiceException {

    public UserAccessDeniedException() {
        super("Cannot access data", HttpStatus.FORBIDDEN);
    }
}