package com.grits.userservice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class PaymentCardNotFoundException extends GlobalServiceException {

    public PaymentCardNotFoundException(UUID card) {
        super("Card " + card + " not found", HttpStatus.NOT_FOUND);
    }
}
