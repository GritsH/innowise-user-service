package com.grits.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grits.userservice.entity.User;
import com.grits.userservice.model.request.user.CreateUserRequest;
import com.grits.userservice.model.request.user.UpdateUserRequest;
import com.grits.userservice.repository.UserRepository;
import com.grits.userservice.util.JwtTestUtils;
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

public class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.of(2000, 1, 1);

    @Test
    @DisplayName("should create user")
    void createUser() throws Exception {
        CreateUserRequest request = createUserRequest();

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.surname").value(request.getSurname()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.active").value(true));

        assertThat(userRepository.count()).isEqualTo(1);

        User saved = userRepository.findAll().getFirst();
        assertThat(saved.getName()).isEqualTo(request.getName());
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    @DisplayName("should return user by id")
    void returnUserById() throws Exception {
        User user = createUser("john");

        mockMvc.perform(get("/v1/users/{id}", user.getId())
                        .with(JwtTestUtils.user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @DisplayName("should update user")
    void updateUser() throws Exception {
        User user = createUser("john");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("updated");
        request.setSurname("doe");
        request.setBirthDate(LocalDate.of(2001, 8, 8));
        request.setEmail(user.getEmail());

        mockMvc.perform(patch("/v1/users/{id}", user.getId())
                        .with(JwtTestUtils.user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated"));

        User updated = userRepository.findById(user.getId()).orElseThrow();

        assertThat(updated.getName()).isEqualTo("updated");
        assertThat(updated.getBirthDate()).isEqualTo(LocalDate.of(2001, 8, 8));
    }

    @Test
    @DisplayName("should deactivate user")
    void deactivateUser() throws Exception {
        User user = createUser("john");

        mockMvc.perform(patch("/v1/users/{id}/deactivate", user.getId())
                        .with(JwtTestUtils.admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        assertThat(userRepository.findById(user.getId()).orElseThrow().isActive()).isFalse();
    }

    @Test
    @DisplayName("should activate user")
    void activateUser() throws Exception {
        User user = createInactiveUser();

        mockMvc.perform(patch("/v1/users/{id}/activate", user.getId())
                        .with(JwtTestUtils.admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        assertThat(userRepository.findById(user.getId()).orElseThrow().isActive()).isTrue();
    }

    @Test
    @DisplayName("should return all users")
    void returnAllUsers() throws Exception {
        userRepository.deleteAll();
        createUser("john");
        createUser("jane");

        mockMvc.perform(get("/v1/users")
                        .with(JwtTestUtils.admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("should filter by name")
    void shouldFilterByName() throws Exception {
        createUser("josef");

        mockMvc.perform(get("/v1/users")
                        .with(JwtTestUtils.admin())
                        .param("name", "josef"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("josef"));
    }

    @Test
    @DisplayName("should return 403 when accessing another user")
    void return403WhenUserDoesNotOwnResource() throws Exception {
        User user = createUser("john");
        mockMvc.perform(get("/v1/users/{id}", UUID.randomUUID())
                        .with(JwtTestUtils.user(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 409 when user with email already exists")
    void return409WhenUserAlreadyExists() throws Exception {
        CreateUserRequest request = createUserRequest();

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("should return 400 when request is invalid")
    void return400WhenRequestInvalid() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("");
        request.setSurname("");
        request.setEmail("abc");

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private User createUser(String name) {
        User user = new User();
        user.setKeycloakUserId(UUID.randomUUID());
        user.setName(name);
        user.setSurname("doe");
        user.setBirthDate(DEFAULT_BIRTH_DATE);
        user.setEmail(name + "@gmail.com");
        user.setActive(true);
        return userRepository.save(user);
    }

    private User createInactiveUser() {
        User user = createUser("john");
        user.setActive(false);
        return userRepository.save(user);
    }

    private CreateUserRequest createUserRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setKeycloakUserId(UUID.randomUUID().toString());
        request.setName("john");
        request.setSurname("doe");
        request.setBirthDate(DEFAULT_BIRTH_DATE);
        request.setEmail("john1@gmail.com");
        return request;
    }
}
