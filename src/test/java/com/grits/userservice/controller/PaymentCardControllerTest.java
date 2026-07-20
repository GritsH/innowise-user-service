package com.grits.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grits.userservice.entity.PaymentCard;
import com.grits.userservice.entity.User;
import com.grits.userservice.model.request.paymentcard.CreateCardRequest;
import com.grits.userservice.model.request.paymentcard.UpdateCardRequest;
import com.grits.userservice.repository.PaymentCardRepository;
import com.grits.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentCardControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PaymentCardRepository paymentCardRepository;

    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("john");
        user.setSurname("doe");
        user.setEmail("john@gmail.com");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setActive(true);
        user = userRepository.save(user);
    }

    @Test
    @DisplayName("should create card")
    void createCard() throws Exception {
        CreateCardRequest request = new CreateCardRequest();
        request.setNumber("1234567890123456");
        request.setHolder("holder");
        request.setExpirationDate(LocalDate.now().plusYears(2));

        mockMvc.perform(post("/v1/cards/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value("holder"))
                .andExpect(jsonPath("$.active").value(true));

        assertThat(paymentCardRepository.countByUserId(user.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("should return card by id")
    void returnCardById() throws Exception {
        PaymentCard card = createTestCard();

        mockMvc.perform(get("/v1/cards/{id}", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId().toString()))
                .andExpect(jsonPath("$.holder").value("holder"));
    }

    @Test
    @DisplayName("should return cards by user id")
    void returnCardsByUserId() throws Exception {
        createTestCard();
        createTestCard("4444444444444444");

        mockMvc.perform(get("/v1/cards/user/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("should return all cards")
    void returnAllCards() throws Exception {
        createTestCard();
        createTestCard("4444444444444444");

        mockMvc.perform(get("/v1/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("should filter cards by holder")
    void filterCardsByHolder() throws Exception {
        createTestCard();
        createTestCard("4444444444444444", "another");

        mockMvc.perform(get("/v1/cards").param("holder", "holder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].holder").value("holder"));
    }

    @Test
    @DisplayName("should update card")
    void updateCard() throws Exception {
        PaymentCard card = createTestCard();

        UpdateCardRequest request = new UpdateCardRequest();
        request.setNumber("9999999999999999");
        request.setHolder("anotherholder");
        request.setExpirationDate(LocalDate.now().plusYears(3));

        mockMvc.perform(patch("/v1/cards/{id}", card.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("9999999999999999"))
                .andExpect(jsonPath("$.holder").value("anotherholder"));

        PaymentCard updated = paymentCardRepository.findById(card.getId()).orElseThrow();

        assertThat(updated.getHolder()).isEqualTo("anotherholder");
    }

    @Test
    @DisplayName("should deactivate card")
    void deactivateCard() throws Exception {
        PaymentCard card = createTestCard();

        mockMvc.perform(patch("/v1/cards/{id}/deactivate", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        assertThat(paymentCardRepository.findById(card.getId()).orElseThrow().isActive()).isFalse();
    }

    @Test
    @DisplayName("should activate card")
    void activateCard() throws Exception {
        PaymentCard card = createTestCard();
        card.setActive(false);

        paymentCardRepository.save(card);

        mockMvc.perform(patch("/v1/cards/{id}/activate", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        assertThat(paymentCardRepository.findById(card.getId()).orElseThrow().isActive()).isTrue();
    }

    @Test
    @DisplayName("should return 400 when card request invalid")
    void validateRequest() throws Exception {
        CreateCardRequest request = new CreateCardRequest();
        request.setNumber("123");
        request.setHolder("");
        request.setExpirationDate(LocalDate.now().minusDays(1));


        mockMvc.perform(post("/v1/cards/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 404 when card does not exist")
    void validateCardDoesNotExist() throws Exception {
        mockMvc.perform(get("/v1/cards/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
    }

    private PaymentCard createTestCard() {
        return createTestCard("1234567890123456", "holder");
    }

    private void createTestCard(String number) {
        createTestCard(number, "holder");
    }

    private PaymentCard createTestCard(String number, String holder) {
        PaymentCard card = new PaymentCard();
        card.setNumber(number);
        card.setHolder(holder);
        card.setExpirationDate(LocalDate.now().plusYears(2));
        card.setActive(true);
        card.setUser(user);
        return paymentCardRepository.save(card);
    }
}
