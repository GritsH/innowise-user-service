package com.grits.userservice.service;

import com.grits.userservice.dao.UserDao;
import com.grits.userservice.entity.User;
import com.grits.userservice.mapper.UserMapper;
import com.grits.userservice.model.request.user.CreateUserRequest;
import com.grits.userservice.model.request.user.UpdateUserRequest;
import com.grits.userservice.model.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserDao userDao;

    private final UserMapper userMapper;

    public UserResponse createUser(CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        user.setActive(true);
        User savedUser = userDao.save(user);

        log.info("User created with email: {}", savedUser.getEmail());
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserById(UUID id) {
        User user = userDao.getUserById(id);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#email")
    public UserResponse getUserByEmail(String email) {
        User user = userDao.getUserByEmail(email);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String name, String surname, int page, int size) {
        return userDao
                .getAllUsers(name, surname, page, size)
                .map(userMapper::toResponse);
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userDao.getUserById(id);
        userMapper.updateEntity(request, user);
        User updatedUser = userDao.saveUpdatedUser(user);

        log.info("User updated with id: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserResponse deactivateUser(UUID id) {
        User user = userDao.deactivateUser(id);
        log.info("User deactivated with id: {}", user.getId());
        return userMapper.toResponse(user);
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserResponse activateUser(UUID id) {
        User user = userDao.activateUser(id);
        log.info("User activated with id: {}", user.getId());
        return userMapper.toResponse(user);
    }
}
