package com.grits.userservice.controller;


import com.grits.userservice.model.request.paymentcard.CreateCardRequest;
import com.grits.userservice.model.request.paymentcard.UpdateCardRequest;
import com.grits.userservice.model.response.paymentcard.PaymentCardResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/cards")
public interface PaymentCardApi {

    @GetMapping
    ResponseEntity<Page<PaymentCardResponse>> getAllCards(
            @RequestParam(required = false) String holder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    );

    @GetMapping("/{id}")
    ResponseEntity<PaymentCardResponse> getCardById(@PathVariable UUID id);

    @GetMapping("/user/{id}")
    ResponseEntity<List<PaymentCardResponse>> getCardsByUserId(@PathVariable UUID id);

    @PostMapping("/user/{id}")
    ResponseEntity<PaymentCardResponse> createCard(@PathVariable UUID id, @Valid @RequestBody CreateCardRequest request);

    @PatchMapping("/{id}")
    ResponseEntity<PaymentCardResponse> updateCard(@PathVariable UUID id, @Valid @RequestBody UpdateCardRequest request);

    @PatchMapping("/{id}/activate")
    ResponseEntity<PaymentCardResponse> activateCard(@PathVariable UUID id);

    @PatchMapping("/{id}/deactivate")
    ResponseEntity<PaymentCardResponse> deactivateCard(@PathVariable UUID id);
}
