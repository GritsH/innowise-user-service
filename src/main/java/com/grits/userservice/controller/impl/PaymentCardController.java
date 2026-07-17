package com.grits.userservice.controller.impl;

import com.grits.userservice.controller.PaymentCardApi;
import com.grits.userservice.model.request.paymentcard.CreateCardRequest;
import com.grits.userservice.model.request.paymentcard.UpdateCardRequest;
import com.grits.userservice.model.response.paymentcard.PaymentCardResponse;
import com.grits.userservice.service.PaymentCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class PaymentCardController implements PaymentCardApi {

    private final PaymentCardService paymentCardService;

    @Autowired
    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @Override
    public ResponseEntity<Page<PaymentCardResponse>> getAllCards(String holder, int page, int size) {
        Page<PaymentCardResponse> cards = paymentCardService.getAllCards(holder, page, size);
        return ResponseEntity.ok(cards);
    }

    @Override
    public ResponseEntity<PaymentCardResponse> getCardById(UUID id) {
        return ResponseEntity.ok(paymentCardService.getCardById(id));
    }

    @Override
    public ResponseEntity<List<PaymentCardResponse>> getCardsByUserId(UUID id) {
        return ResponseEntity.ok(paymentCardService.getCardsByUserId(id));
    }

    @Override
    public ResponseEntity<PaymentCardResponse> createCard(UUID id, CreateCardRequest request) {
        return ResponseEntity.ok(paymentCardService.createCard(id, request));
    }

    @Override
    public ResponseEntity<PaymentCardResponse> updateCard(UUID id, UpdateCardRequest request) {
        return ResponseEntity.ok(paymentCardService.updateCard(id, request));
    }

    @Override
    public ResponseEntity<PaymentCardResponse> activateCard(UUID id) {
        return ResponseEntity.ok(paymentCardService.activateCard(id));
    }

    @Override
    public ResponseEntity<PaymentCardResponse> deactivateCard(UUID id) {
        return ResponseEntity.ok(paymentCardService.deactivateCard(id));
    }
}
