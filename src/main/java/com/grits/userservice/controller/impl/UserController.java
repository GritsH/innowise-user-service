package com.grits.userservice.controller.impl;

import com.grits.userservice.controller.UserApi;
import com.grits.userservice.model.request.user.CreateUserRequest;
import com.grits.userservice.model.request.user.UpdateUserRequest;
import com.grits.userservice.model.response.user.UserResponse;
import com.grits.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController implements UserApi {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<Page<UserResponse>> getAllUsers(String name, String surname, int page, int size) {
        Page<UserResponse> users = userService.getAllUsers(name, surname, page, size);
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Override
    public ResponseEntity<UserResponse> createUser(CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(UUID id, UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @Override
    public ResponseEntity<UserResponse> deactivateUser(UUID id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @Override
    public ResponseEntity<UserResponse> activateUser(UUID id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }
}
