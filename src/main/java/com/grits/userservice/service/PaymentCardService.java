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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCardService {

    private final PaymentCardDao paymentCardDao;

    private final UserDao userDao;

    private final PaymentCardMapper paymentCardMapper;

    @Transactional
    public PaymentCardResponse createCard(UUID userId, CreateCardRequest request) {
        log.info("Creating card for user {}", userId);

        User user = userDao.getUserById(userId);
        if (paymentCardDao.countCardsByUserId(userId) >= 5) {
            throw new MaxCardAmountException(userId);
        }
        PaymentCard card = paymentCardMapper.toEntity(request);
        card.setUser(user);
        card.setActive(true);
        PaymentCard savedCard = paymentCardDao.save(card);

        log.info("Card created for user {}", userId);
        return paymentCardMapper.toResponse(savedCard);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cards", key = "#id")
    public PaymentCardResponse getCardById(UUID id) {
        log.info("Getting card with id {}", id);
        PaymentCard card = paymentCardDao.getPaymentCardById(id);
        return paymentCardMapper.toResponse(card);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "userCards", key = "#userId")
    public List<PaymentCardResponse> getCardsByUserId(UUID userId) {
        log.info("Getting cards for user {}", userId);
        return paymentCardDao
                .getPaymentCardsByUserId(userId)
                .stream()
                .map(paymentCardMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PaymentCardResponse> getAllCards(String holder, int page, int size) {
        log.info("Getting all cards");
        return paymentCardDao
                .getAllCards(holder, page, size)
                .map(paymentCardMapper::toResponse);
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "cards", key = "#id"),
            evict = @CacheEvict(value = "userCards", key = "#result.userId")
    )
    public PaymentCardResponse updateCard(UUID id, UpdateCardRequest request) {
        log.info("Updating card with id {}", id);

        PaymentCard card = paymentCardDao.getPaymentCardById(id);
        paymentCardMapper.updateEntity(request, card);
        PaymentCard updatedCard = paymentCardDao.save(card);

        log.info("Card updated with id {}", id);
        return paymentCardMapper.toResponse(updatedCard);
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "cards", key = "#id"),
            evict = @CacheEvict(value = "userCards", key = "#result.userId")
    )
    public PaymentCardResponse deactivateCard(UUID id) {
        log.info("Deactivating card with id {}", id);
        PaymentCard card = paymentCardDao.deactivatePaymentCard(id);
        log.info("Card deactivated with id {}", id);
        return paymentCardMapper.toResponse(card);
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "cards", key = "#id"),
            evict = @CacheEvict(value = "userCards", key = "#result.userId")
    )
    public PaymentCardResponse activateCard(UUID id) {
        log.info("Activating card with id {}", id);
        PaymentCard card = paymentCardDao.activatePaymentCard(id);
        log.info("Card activated with id {}", id);
        return paymentCardMapper.toResponse(card);
    }
}
