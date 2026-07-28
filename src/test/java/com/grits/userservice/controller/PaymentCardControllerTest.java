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
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentCardControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PaymentCardRepository paymentCardRepository;

    @Autowired
    UserRepository userRepository;

    private User user;
    private static final String DEFAULT_HOLDER = "holder";
    private static final LocalDate DEFAULT_EXPIRATION = LocalDate.of(2030, 1, 1);

    @BeforeEach
    void setUp() {
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();
        user = createUser();
    }

    @Test
    @DisplayName("should create card")
    void createNewCard() throws Exception {
        CreateCardRequest request = createCardRequest();

        mockMvc.perform(post("/v1/cards/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.holder").value(DEFAULT_HOLDER))
                .andExpect(jsonPath("$.active").value(true));

        assertThat(paymentCardRepository.countByUserId(user.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("should return card by id")
    void returnCardById() throws Exception {
        PaymentCard card = createCard();

        mockMvc.perform(get("/v1/cards/{id}", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId().toString()))
                .andExpect(jsonPath("$.holder").value(DEFAULT_HOLDER));
    }

    @Test
    @DisplayName("should return cards by user id")
    void returnCardsByUserId() throws Exception {
        createCard();
        createCard(generate());

        mockMvc.perform(get("/v1/cards/user/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("should return all cards")
    void returnAllCards() throws Exception {
        createCard();
        createCard(generate());

        mockMvc.perform(get("/v1/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("should filter cards by holder")
    void filterCardsByHolder() throws Exception {
        createCard();
        createCard();
        createCard(generate(), "another holder");

        mockMvc.perform(get("/v1/cards")
                        .param("holder", "another holder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].holder").value("another holder"));
    }

    @Test
    @DisplayName("should update card")
    void updateCard() throws Exception {
        PaymentCard card = createCard();

        UpdateCardRequest request = new UpdateCardRequest();
        request.setNumber("9999999999999999");
        request.setHolder("updated-holder");
        request.setExpirationDate(LocalDate.of(2032, 1, 1));

        mockMvc.perform(patch("/v1/cards/{id}", card.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("9999999999999999"))
                .andExpect(jsonPath("$.holder").value("updated-holder"));

        PaymentCard updated = paymentCardRepository.findById(card.getId()).orElseThrow();

        assertThat(updated.getHolder()).isEqualTo("updated-holder");
        assertThat(updated.getNumber()).isEqualTo("9999999999999999");
    }

    @Test
    @DisplayName("should deactivate card")
    void deactivateCard() throws Exception {
        PaymentCard card = createCard();

        mockMvc.perform(patch("/v1/cards/{id}/deactivate", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        assertThat(paymentCardRepository.findById(card.getId()).orElseThrow().isActive()).isFalse();
    }

    @Test
    @DisplayName("should activate card")
    void activateCard() throws Exception {
        PaymentCard card = createInactiveCard();

        mockMvc.perform(patch("/v1/cards/{id}/activate", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        assertThat(paymentCardRepository.findById(card.getId()).orElseThrow().isActive()).isTrue();
    }

    @Test
    @DisplayName("should return 400 when request is invalid")
    void return400WhenRequestIsInvalid() throws Exception {
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
    @DisplayName("should return 409 when card already exists")
    void return409WhenCardAlreadyExists() throws Exception {
        CreateCardRequest request = createCardRequest();

        mockMvc.perform(post("/v1/cards/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/cards/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("should return 404 when card does not exist")
    void return404WhenCardDoesNotExist() throws Exception {
        mockMvc.perform(get("/v1/cards/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 500 when unexpected exception happens")
    void return500WhenInternalErrorOccurs() throws Exception {
        mockMvc.perform(get("/v1/cards/not-a-valid-uuid"))
                .andExpect(status().isInternalServerError());
    }

    private User createUser() {
        User newUser = new User();
        newUser.setKeycloakUserId(UUID.randomUUID());
        newUser.setName("john");
        newUser.setSurname("doe");
        newUser.setEmail("john@gmail.com");
        newUser.setBirthDate(LocalDate.of(2000, 1, 1));
        newUser.setActive(true);
        return userRepository.save(newUser);
    }

    private PaymentCard createCard() {
        return createCard(generate(), DEFAULT_HOLDER);
    }

    private PaymentCard createCard(String number) {
        return createCard(number, DEFAULT_HOLDER);
    }

    private PaymentCard createCard(String number, String holder) {
        PaymentCard card = new PaymentCard();
        card.setNumber(number);
        card.setHolder(holder);
        card.setExpirationDate(DEFAULT_EXPIRATION);
        card.setActive(true);
        card.setUser(user);
        return paymentCardRepository.save(card);
    }

    private PaymentCard createInactiveCard() {
        PaymentCard card = createCard();
        card.setActive(false);
        return paymentCardRepository.save(card);
    }

    private CreateCardRequest createCardRequest() {
        CreateCardRequest request = new CreateCardRequest();
        request.setNumber(generate());
        request.setHolder(DEFAULT_HOLDER);
        request.setExpirationDate(DEFAULT_EXPIRATION);
        return request;
    }

    private String generate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder cardNumber = new StringBuilder(16);
        cardNumber.append(random.nextInt(1, 10));
        for (int i = 1; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }
}
