package com.grits.userservice.exception;

import org.springframework.http.HttpStatus;

public class PaymentCardAlreadyExistsException extends GlobalServiceException {

    public PaymentCardAlreadyExistsException(String number) {
        super("Card with number " + number + " already exists", HttpStatus.CONFLICT);
    }
}
