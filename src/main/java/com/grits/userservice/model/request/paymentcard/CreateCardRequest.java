package com.grits.userservice.model.request.paymentcard;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCardRequest {

    @NotBlank(message = "Card number required")
    @Pattern(
            regexp = "\\d{16}",
            message = "Card number must contain 16 digits"
    )
    private String number;


    @NotBlank(message = "Holder required")
    @Size(max = 100, message = "Holder must be less than 100 characters long")
    private String holder;

    @NotNull(message = "Expiration date required")
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;
}
