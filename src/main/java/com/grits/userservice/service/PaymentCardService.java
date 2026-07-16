package com.grits.userservice.service;

import com.grits.userservice.dao.PaymentCardDao;
import com.grits.userservice.dao.UserDao;
import com.grits.userservice.entity.PaymentCard;
import com.grits.userservice.entity.User;
import com.grits.userservice.exception.MaxCardAmountException;
import com.grits.userservice.mapper.PaymentCardMapper;
import com.grits.userservice.model.request.paymentcard.CreateCardRequest;
import com.grits.userservice.model.request.paymentcard.UpdateCardRequest;
import com.grits.userservice.model.response.paymentcard.PaymentCardResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentCardService {

    private final PaymentCardDao paymentCardDao;

    private final UserDao userDao;

    private final PaymentCardMapper paymentCardMapper;

    @Transactional
    public PaymentCardResponse createCard(UUID userId, CreateCardRequest request) {
        User user = userDao.getUserById(userId);
        if (user.getPaymentCards().size() >= 5) {
            throw new MaxCardAmountException(userId);
        }
        PaymentCard card = paymentCardMapper.toEntity(request);
        card.setUser(user);
        card.setActive(true);
        PaymentCard savedCard = paymentCardDao.save(card);
        return paymentCardMapper.toResponse(savedCard);
    }

    public PaymentCardResponse getCardById(UUID id) {
        PaymentCard card = paymentCardDao.getPaymentCardById(id);
        return paymentCardMapper.toResponse(card);
    }

    public List<PaymentCardResponse> getCardsByUserId(UUID userId) {
        return paymentCardDao
                .getPaymentCardsByUserId(userId)
                .stream()
                .map(paymentCardMapper::toResponse)
                .toList();
    }

    public Page<PaymentCardResponse> getAllCards(String holder, int page, int size) {
        return paymentCardDao
                .getAllCards(holder, page, size)
                .map(paymentCardMapper::toResponse);
    }

    @Transactional
    public PaymentCardResponse updateCard(UUID id, UpdateCardRequest request) {
        PaymentCard card = paymentCardDao.getPaymentCardById(id);
        paymentCardMapper.updateEntity(request, card);
        PaymentCard updatedCard = paymentCardDao.save(card);
        return paymentCardMapper.toResponse(updatedCard);
    }

    @Transactional
    public PaymentCardResponse deactivateCard(UUID id) {
        PaymentCard card = paymentCardDao.deactivatePaymentCard(id);
        return paymentCardMapper.toResponse(card);
    }

    @Transactional
    public PaymentCardResponse activateCard(UUID id) {
        PaymentCard card = paymentCardDao.activatePaymentCard(id);
        return paymentCardMapper.toResponse(card);
    }
}
