package com.grits.userservice.controller;

import com.grits.userservice.model.request.user.CreateUserRequest;
import com.grits.userservice.model.request.user.UpdateUserRequest;
import com.grits.userservice.model.response.user.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RequestMapping("/v1/users")
public interface UserApi {

    @GetMapping
    ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    );

    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getUserById(@PathVariable UUID id);

    @PostMapping
    ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request);

    @PatchMapping("/{id}")
    ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request);

    @PatchMapping("/{id}/deactivate")
    ResponseEntity<UserResponse> deactivateUser(@PathVariable UUID id);

    @PatchMapping("/{id}/activate")
    ResponseEntity<UserResponse> activateUser(@PathVariable UUID id);
}
