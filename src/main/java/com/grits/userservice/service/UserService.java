package com.grits.userservice.service;

import com.grits.userservice.dao.UserDao;
import com.grits.userservice.entity.User;
import com.grits.userservice.mapper.UserMapper;
import com.grits.userservice.model.request.user.CreateUserRequest;
import com.grits.userservice.model.request.user.UpdateUserRequest;
import com.grits.userservice.model.response.user.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    private final UserMapper userMapper;

    public UserResponse createUser(CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        user.setActive(true);
        User savedUser = userDao.save(user);
        return userMapper.toResponse(savedUser);
    }

    public UserResponse getUserById(UUID id) {
        User user = userDao.getUserById(id);
        return userMapper.toResponse(user);
    }

    public Page<UserResponse> getAllUsers(String name, String surname, int page, int size) {
        return userDao
                .getAllUsers(name, surname, page, size)
                .map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userDao.getUserById(id);
        userMapper.updateEntity(request, user);
        User updatedUser = userDao.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public UserResponse deactivateUser(UUID id) {
        User user = userDao.deactivateUser(id);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse activateUser(UUID id) {
        User user = userDao.activateUser(id);
        return userMapper.toResponse(user);
    }
}
