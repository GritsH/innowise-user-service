package com.grits.userservice.dao;

import com.grits.userservice.entity.PaymentCard;
import com.grits.userservice.exception.PaymentCardAlreadyExistsException;
import com.grits.userservice.exception.PaymentCardNotFoundException;
import com.grits.userservice.repository.PaymentCardRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCardDaoTest {

    @Mock
    private PaymentCardRepository repository;

    @InjectMocks
    private PaymentCardDao paymentCardDao;

    private PaymentCard paymentCard;
    private UUID cardId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();
        userId = UUID.randomUUID();

        paymentCard = new PaymentCard();
        paymentCard.setId(cardId);
        paymentCard.setNumber("1111111111111111");
        paymentCard.setActive(true);
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("should save a new payment card")
    void save() {
        when(repository.existsByNumber(paymentCard.getNumber())).thenReturn(false);
        when(repository.save(paymentCard)).thenReturn(paymentCard);

        PaymentCard result = paymentCardDao.save(paymentCard);

        assertThat(result).isEqualTo(paymentCard);

        verify(repository).existsByNumber(paymentCard.getNumber());
        verify(repository).save(paymentCard);
    }

    @Test
    @DisplayName("should throw exception if card already exists")
    void saveWithException() {
        when(repository.existsByNumber(paymentCard.getNumber())).thenReturn(true);

        assertThatThrownBy(() -> paymentCardDao.save(paymentCard)).isInstanceOf(PaymentCardAlreadyExistsException.class);

        verify(repository).existsByNumber(paymentCard.getNumber());
    }

    @Test
    @DisplayName("should save updated payment card")
    void saveUpdatedPaymentCard() {
        when(repository.save(paymentCard)).thenReturn(paymentCard);

        PaymentCard result = paymentCardDao.saveUpdatedPaymentCard(paymentCard);

        assertThat(result).isEqualTo(paymentCard);

        verify(repository).save(paymentCard);
    }

    @Test
    @DisplayName("should get payment card by id")
    void getPaymentCardById() {
        when(repository.findById(cardId)).thenReturn(Optional.of(paymentCard));

        PaymentCard result = paymentCardDao.getPaymentCardById(cardId);

        assertThat(result).isEqualTo(paymentCard);

        verify(repository).findById(cardId);
    }

    @Test
    @DisplayName("should throw exception if payment card does not exist")
    void getPaymentCardByIdWithException() {
        when(repository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentCardDao.getPaymentCardById(cardId)).isInstanceOf(PaymentCardNotFoundException.class);

        verify(repository).findById(cardId);
    }

    @Test
    @DisplayName("should get payment cards by user id")
    void getPaymentCardsByUserId() {
        List<PaymentCard> cards = List.of(paymentCard);

        when(repository.findAllByUserId(userId)).thenReturn(cards);

        List<PaymentCard> result = paymentCardDao.getPaymentCardsByUserId(userId);

        assertThat(result).isEqualTo(cards);

        verify(repository).findAllByUserId(userId);
    }

    @Test
    @DisplayName("should return owner's Keycloak id")
    void getOwnerKeycloakId() {
        UUID keycloakUserId = UUID.randomUUID();

        when(repository.findOwnerKeycloakId(paymentCard.getId())).thenReturn(Optional.of(keycloakUserId));

        UUID result = paymentCardDao.findOwnerKeycloakId(cardId);

        assertThat(result).isEqualTo(keycloakUserId);

        verify(repository).findOwnerKeycloakId(cardId);
    }

    @Test
    @DisplayName("should throw when card does not exist")
    void shouldThrowWhenCardDoesNotExist() {
        when(repository.findOwnerKeycloakId(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentCardDao.findOwnerKeycloakId(cardId)).isInstanceOf(PaymentCardNotFoundException.class);

        verify(repository).findOwnerKeycloakId(cardId);
    }

    @Test
    @DisplayName("should deactivate payment card")
    void deactivatePaymentCard() {
        when(repository.findById(cardId)).thenReturn(Optional.of(paymentCard));
        when(repository.save(paymentCard)).thenReturn(paymentCard);

        PaymentCard result = paymentCardDao.deactivatePaymentCard(cardId);

        assertThat(result.isActive()).isFalse();

        verify(repository).findById(cardId);
        verify(repository).save(paymentCard);
    }

    @Test
    @DisplayName("should activate payment card")
    void activatePaymentCard() {
        paymentCard.setActive(false);

        when(repository.findById(cardId)).thenReturn(Optional.of(paymentCard));
        when(repository.save(paymentCard)).thenReturn(paymentCard);

        PaymentCard result = paymentCardDao.activatePaymentCard(cardId);

        assertThat(result.isActive()).isTrue();

        verify(repository).findById(cardId);
        verify(repository).save(paymentCard);
    }

    @Test
    @DisplayName("should get all payment cards")
    void getAllCards() {
        Page<PaymentCard> cardPage = new PageImpl<>(List.of(paymentCard));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(cardPage);

        Page<PaymentCard> result = paymentCardDao.getAllCards(null, 0, 10);

        assertThat(result).isEqualTo(cardPage);
    }

    @Test
    @DisplayName("should count payment cards by user id")
    void countCardsByUserId() {
        when(repository.countByUserId(userId)).thenReturn(5);

        int result = paymentCardDao.countCardsByUserId(userId);

        assertThat(result).isEqualTo(5);

        verify(repository).countByUserId(userId);
    }
}