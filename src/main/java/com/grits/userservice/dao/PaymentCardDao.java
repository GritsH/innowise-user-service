package com.grits.userservice.dao;

import com.grits.userservice.entity.PaymentCard;
import com.grits.userservice.exception.PaymentCardAlreadyExistsException;
import com.grits.userservice.exception.PaymentCardNotFoundException;
import com.grits.userservice.repository.PaymentCardRepository;
import com.grits.userservice.specification.PaymentCardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentCardDao {

    private final PaymentCardRepository paymentCardRepository;

    public PaymentCard save(PaymentCard paymentCard) {
        if (paymentCardRepository.existsByNumber(paymentCard.getNumber())) {
            throw new PaymentCardAlreadyExistsException(paymentCard.getNumber());
        }
        return paymentCardRepository.save(paymentCard);
    }

    public PaymentCard saveUpdatedPaymentCard(PaymentCard paymentCard) {
        return paymentCardRepository.save(paymentCard);
    }

    public PaymentCard getPaymentCardById(UUID id) {
        return paymentCardRepository.findById(id).orElseThrow(
                () -> new PaymentCardNotFoundException(id)
        );
    }

    public List<PaymentCard> getPaymentCardsByUserId(UUID userId) {
        return paymentCardRepository.findAllByUserId(userId);
    }

    public PaymentCard deactivatePaymentCard(UUID id) {
        PaymentCard paymentCard = getPaymentCardById(id);
        paymentCard.setActive(false);
        return paymentCardRepository.save(paymentCard);
    }

    public PaymentCard activatePaymentCard(UUID id) {
        PaymentCard paymentCard = getPaymentCardById(id);
        paymentCard.setActive(true);
        return paymentCardRepository.save(paymentCard);
    }


    public Page<PaymentCard> getAllCards(String holder, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("expirationDate").ascending());
        Specification<PaymentCard> specification = Specification.allOf(PaymentCardSpecification.hasHolder(holder));
        return paymentCardRepository.findAll(specification, pageable);
    }

    public int countCardsByUserId(UUID userId) {
        return paymentCardRepository.countByUserId(userId);
    }
}
