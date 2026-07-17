package com.grits.userservice.exception.handler;

import com.grits.userservice.exception.GlobalServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public @ResponseBody ErrorResponse handleInternalServerError(Exception ex) {
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(value = GlobalServiceException.class)
    public @ResponseBody ErrorResponse handleExceptions(GlobalServiceException ex) {
        return ErrorResponse.create(ex, ex.getStatusCode(), ex.getMessage());
    }
}
