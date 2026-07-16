package com.grits.userservice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class MaxCardAmountException extends GlobalServiceException {

    public MaxCardAmountException(UUID user) {
        super("User " + user + " maxed out the amount of cards (5)", HttpStatus.CONFLICT);
    }
}
