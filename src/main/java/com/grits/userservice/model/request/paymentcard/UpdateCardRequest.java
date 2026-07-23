package com.grits.userservice.model.request.paymentcard;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateCardRequest {

    @Pattern(
            regexp = "\\d{16}",
            message = "Card number must contain 16 digits"
    )
    private String number;

    private String holder;

    @NotNull(message = "Expiration date required")
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;
}
