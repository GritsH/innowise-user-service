package com.grits.userservice.controller;

import com.grits.userservice.model.request.paymentcard.CreateCardRequest;
import com.grits.userservice.model.request.paymentcard.UpdateCardRequest;
import com.grits.userservice.model.response.paymentcard.PaymentCardResponse;
import com.grits.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/cards")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @Autowired
    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentCardResponse>> getAllCards(
            @RequestParam(required = false) String holder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<PaymentCardResponse> cards = paymentCardService.getAllCards(holder, page, size);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentCardResponse> getCardById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentCardService.getCardById(id));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentCardResponse>> getCardsByUserId(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentCardService.getCardsByUserId(id));
    }

    @PostMapping("/user/{id}")
    public ResponseEntity<PaymentCardResponse> createCard(@PathVariable UUID id, @Valid @RequestBody CreateCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardService.createCard(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PaymentCardResponse> updateCard(@PathVariable UUID id, @Valid @RequestBody UpdateCardRequest request) {
        return ResponseEntity.ok(paymentCardService.updateCard(id, request));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentCardResponse> activateCard(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentCardService.activateCard(id));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentCardResponse> deactivateCard(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentCardService.deactivateCard(id));
    }
}
