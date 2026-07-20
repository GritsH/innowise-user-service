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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceTest {

    @Mock
    private PaymentCardDao paymentCardDao;

    @Mock
    private UserDao userDao;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @InjectMocks
    private PaymentCardService paymentCardService;

    private User user;
    private PaymentCard paymentCard;
    private PaymentCardResponse paymentCardResponse;
    private CreateCardRequest createCardRequest;
    private UpdateCardRequest updateCardRequest;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        paymentCard = mock(PaymentCard.class);
        paymentCardResponse = mock(PaymentCardResponse.class);
        createCardRequest = mock(CreateCardRequest.class);
        updateCardRequest = mock(UpdateCardRequest.class);
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(paymentCardDao, userDao, paymentCardMapper);
    }

    @Test
    @DisplayName("should create payment card")
    void createCard() {
        UUID userId = UUID.randomUUID();

        when(userDao.getUserById(userId)).thenReturn(user);
        when(paymentCardDao.countCardsByUserId(userId)).thenReturn(0);
        when(paymentCardMapper.toEntity(createCardRequest)).thenReturn(paymentCard);
        when(paymentCardDao.save(paymentCard)).thenReturn(paymentCard);
        when(paymentCardMapper.toResponse(paymentCard)).thenReturn(paymentCardResponse);

        PaymentCardResponse result = paymentCardService.createCard(userId, createCardRequest);

        verify(userDao).getUserById(userId);
        verify(paymentCardDao).countCardsByUserId(userId);
        verify(paymentCardDao).save(paymentCard);

        assertThat(result).isSameAs(paymentCardResponse);
    }

    @Test
    @DisplayName("should throw exception when user already has 5 cards")
    void createCard_shouldThrowException() {
        UUID userId = UUID.randomUUID();

        when(userDao.getUserById(userId)).thenReturn(user);
        when(paymentCardDao.countCardsByUserId(userId)).thenReturn(5);

        assertThatThrownBy(() -> paymentCardService.createCard(userId, createCardRequest)).isInstanceOf(MaxCardAmountException.class);

        verify(userDao).getUserById(userId);
        verify(paymentCardDao).countCardsByUserId(userId);
    }

    @Test
    @DisplayName("should get card by id")
    void getCardById() {
        UUID id = UUID.randomUUID();

        when(paymentCardDao.getPaymentCardById(id)).thenReturn(paymentCard);
        when(paymentCardMapper.toResponse(paymentCard)).thenReturn(paymentCardResponse);

        PaymentCardResponse result = paymentCardService.getCardById(id);

        verify(paymentCardDao).getPaymentCardById(id);

        assertThat(result).isEqualTo(paymentCardResponse);
    }

    @Test
    @DisplayName("should get cards by user id")
    void getCardsByUserId() {
        UUID userId = UUID.randomUUID();
        List<PaymentCard> cards = List.of(paymentCard);

        when(paymentCardDao.getPaymentCardsByUserId(userId)).thenReturn(cards);
        when(paymentCardMapper.toResponse(paymentCard)).thenReturn(paymentCardResponse);

        List<PaymentCardResponse> result = paymentCardService.getCardsByUserId(userId);

        verify(paymentCardDao).getPaymentCardsByUserId(userId);
        verify(paymentCardMapper).toResponse(paymentCard);

        assertThat(result).containsExactly(paymentCardResponse);
    }

    @Test
    @DisplayName("should get all cards")
    void getAllCards() {
        Page<PaymentCard> cards = new PageImpl<>(List.of(paymentCard));

        when(paymentCardDao.getAllCards(null, 0, 10)).thenReturn(cards);
        when(paymentCardMapper.toResponse(paymentCard)).thenReturn(paymentCardResponse);

        Page<PaymentCardResponse> result = paymentCardService.getAllCards(null, 0, 10);

        verify(paymentCardDao).getAllCards(null, 0, 10);

        assertThat(result.getContent()).containsExactly(paymentCardResponse);
    }

    @Test
    @DisplayName("should update card")
    void updateCard() {
        UUID id = UUID.randomUUID();

        when(paymentCardDao.getPaymentCardById(id)).thenReturn(paymentCard);
        when(paymentCardDao.save(paymentCard)).thenReturn(paymentCard);
        when(paymentCardMapper.toResponse(paymentCard)).thenReturn(paymentCardResponse);

        PaymentCardResponse result = paymentCardService.updateCard(id, updateCardRequest);

        verify(paymentCardDao).getPaymentCardById(id);
        verify(paymentCardMapper).updateEntity(updateCardRequest, paymentCard);
        verify(paymentCardDao).save(paymentCard);

        assertThat(result).isEqualTo(paymentCardResponse);
    }

    @Test
    @DisplayName("should deactivate card")
    void deactivateCard() {
        UUID id = UUID.randomUUID();

        when(paymentCardDao.deactivatePaymentCard(id)).thenReturn(paymentCard);
        when(paymentCardMapper.toResponse(paymentCard)).thenReturn(paymentCardResponse);

        PaymentCardResponse result = paymentCardService.deactivateCard(id);

        verify(paymentCardDao).deactivatePaymentCard(id);

        assertThat(result).isEqualTo(paymentCardResponse);
    }

    @Test
    @DisplayName("should activate card")
    void activateCard() {
        UUID id = UUID.randomUUID();

        when(paymentCardDao.activatePaymentCard(id)).thenReturn(paymentCard);
        when(paymentCardMapper.toResponse(paymentCard)).thenReturn(paymentCardResponse);

        PaymentCardResponse result = paymentCardService.activateCard(id);

        verify(paymentCardDao).activatePaymentCard(id);

        assertThat(result).isEqualTo(paymentCardResponse);
    }
}