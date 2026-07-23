package com.grits.userservice.model.response.paymentcard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardResponse {

    private UUID id;

    private String number;

    private String holder;

    private LocalDate expirationDate;

    private boolean active;

    private UUID userId;
}
