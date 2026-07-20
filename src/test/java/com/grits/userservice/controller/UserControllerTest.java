package com.grits.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grits.userservice.entity.User;
import com.grits.userservice.model.request.user.CreateUserRequest;
import com.grits.userservice.model.request.user.UpdateUserRequest;
import com.grits.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
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

    @Test
    @DisplayName("should create user")
    void createUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("name");
        request.setSurname("surname");
        request.setBirthDate(LocalDate.of(2000, 1, 19));
        request.setEmail("name_surname@test.com");

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.surname").value("surname"))
                .andExpect(jsonPath("$.email").value("name_surname@test.com"))
                .andExpect(jsonPath("$.active").value(true));

        assertThat(userRepository.count()).isEqualTo(1);

        User saved = userRepository.findAll().getFirst();

        assertThat(saved.getName()).isEqualTo("name");
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    @DisplayName("should return user by id")
    void returnUserById() throws Exception {
        User user = new User();

        user.setName("name");
        user.setSurname("surname");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail("name@gmail.com");
        user.setActive(true);

        user = userRepository.save(user);

        mockMvc.perform(get("/v1/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.email").value("name@gmail.com"));
    }

    @Test
    @DisplayName("should update user")
    void updateUser() throws Exception {
        User user = new User();
        user.setName("name");
        user.setSurname("surname");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail("name@gmail.com");
        user = userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("anothername");
        request.setSurname("surname");
        request.setBirthDate(LocalDate.of(2001, 8, 8));
        request.setEmail("name@gmail.com");

        mockMvc.perform(patch("/v1/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("anothername"))
                .andExpect(jsonPath("$.surname").value("surname"));

        User updated = userRepository.findById(user.getId()).orElseThrow();

        assertThat(updated.getName()).isEqualTo("anothername");
        assertThat(updated.getSurname()).isEqualTo("surname");
    }

    @Test
    @DisplayName("should deactivate user")
    void deactivateUser() throws Exception {
        User user = new User();
        user.setName("name");
        user.setSurname("surname");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail("name@gmail.com");
        user.setActive(true);
        user = userRepository.save(user);

        mockMvc.perform(patch("/v1/users/{id}/deactivate", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        User updated = userRepository.findById(user.getId()).orElseThrow();

        assertThat(updated.isActive()).isFalse();
    }

    @Test
    @DisplayName("should activate user")
    void activateUser() throws Exception {
        User user = new User();
        user.setName("name");
        user.setSurname("surname");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail("name@gmail.com");
        user.setActive(false);
        user = userRepository.save(user);

        mockMvc.perform(patch("/v1/users/{id}/activate", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        assertThat(userRepository.findById(user.getId()).orElseThrow().isActive()).isTrue();
    }

    @Test
    @DisplayName("should return all users")
    void returnAllUsers() throws Exception {
        User john = new User();
        john.setName("john");
        john.setSurname("doe");
        john.setBirthDate(LocalDate.of(2000, 1, 1));
        john.setEmail("john@gmail.com");

        User jane = new User();
        jane.setName("jane");
        jane.setSurname("doe");
        jane.setBirthDate(LocalDate.of(2000, 2, 2));
        jane.setEmail("jane@gmail.com");

        userRepository.saveAll(List.of(john, jane));

        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("should filter by name")
    void shouldFilterBySurname() throws Exception {
        User john = new User();
        john.setName("john");
        john.setSurname("doe");
        john.setBirthDate(LocalDate.of(2000, 1, 1));
        john.setEmail("john@gmail.com");

        User jane = new User();
        jane.setName("jane");
        jane.setSurname("doe");
        jane.setBirthDate(LocalDate.of(2000, 2, 2));
        jane.setEmail("jane@gmail.com");

        userRepository.saveAll(List.of(john, jane));

        mockMvc.perform(get("/v1/users")
                        .param("name", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("john"));
    }

    @Test
    @DisplayName("should return 404 when user does not exist")
    void return404WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/v1/users/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
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
}
