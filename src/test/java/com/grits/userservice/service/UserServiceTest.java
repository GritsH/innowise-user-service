package com.grits.userservice.service;

import com.grits.userservice.dao.UserDao;
import com.grits.userservice.entity.User;
import com.grits.userservice.mapper.UserMapper;
import com.grits.userservice.model.request.user.CreateUserRequest;
import com.grits.userservice.model.request.user.UpdateUserRequest;
import com.grits.userservice.model.response.user.UserResponse;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        createUserRequest = mock(CreateUserRequest.class);
        updateUserRequest = mock(UpdateUserRequest.class);
        userResponse = mock(UserResponse.class);
    }

    @AfterEach
    public void after() {
        verifyNoMoreInteractions(userDao, userMapper);
    }

    @Test
    @DisplayName("should create new user")
    void createUser() {
        when(userMapper.toEntity(createUserRequest)).thenReturn(user).thenReturn(user);
        when(userDao.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(createUserRequest);

        verify(userDao).save(user);

        assertThat(result).isSameAs(userResponse);
    }

    @Test
    @DisplayName("should get user by id")
    void getUserById() {
        UUID id = UUID.randomUUID();

        when(userDao.getUserById(id)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(id);

        verify(userDao).getUserById(id);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("should get all users")
    void getAllUsers() {
        Page<User> users = new PageImpl<>(List.of(user));

        when(userDao.getAllUsers(null, null, 0, 10)).thenReturn(users);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        Page<UserResponse> result = userService.getAllUsers(null, null, 0, 10);

        verify(userDao).getAllUsers(null, null, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("should update user")
    void updateUser() {
        UUID id = UUID.randomUUID();

        when(userDao.getUserById(id)).thenReturn(user);
        when(userDao.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUser(id, updateUserRequest);

        verify(userDao).getUserById(id);
        verify(userMapper).updateEntity(updateUserRequest, user);
        verify(userDao).save(user);

        assertThat(result).isEqualTo(userResponse);
    }

    @Test
    @DisplayName("should deactivate user")
    void deactivateUser() {
        UUID id = UUID.randomUUID();

        when(userDao.deactivateUser(id)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.deactivateUser(id);

        verify(userDao).deactivateUser(id);

        assertThat(result).isEqualTo(userResponse);
    }

    @Test
    @DisplayName("should activate user")
    void activateUser() {
        UUID id = UUID.randomUUID();

        when(userDao.activateUser(id)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.activateUser(id);

        verify(userDao).activateUser(id);

        assertThat(result).isEqualTo(userResponse);
    }
}