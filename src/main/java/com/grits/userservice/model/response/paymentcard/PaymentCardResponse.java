package com.grits.userservice.model.response.paymentcard;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentCardResponse {

    private UUID id;

    private String number;

    private String holder;

    private LocalDate expirationDate;

    private boolean active;

    private UUID userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
