package com.grits.userservice.service;

import com.grits.userservice.dao.UserDao;
import com.grits.userservice.entity.User;
import com.grits.userservice.exception.UserAlreadyExistsException;
import com.grits.userservice.exception.UserNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
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
        when(userMapper.toEntity(createUserRequest)).thenReturn(user);
        when(userDao.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(createUserRequest);

        assertThat(result).isSameAs(userResponse);

        verify(userMapper).toEntity(createUserRequest);
        verify(userDao).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("should throw exception if user already exists")
    void createUserThrowException() {
        when(userMapper.toEntity(createUserRequest)).thenReturn(user).thenReturn(user);
        doThrow(UserAlreadyExistsException.class).when(userDao).save(user);

        assertThatThrownBy(() -> userService.createUser(createUserRequest)).isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    @DisplayName("should get user by id")
    void getUserById() {
        UUID id = UUID.randomUUID();

        when(userDao.getUserById(id)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(id);

        assertThat(result).isNotNull();

        verify(userDao).getUserById(id);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("should get user by email")
    void getUserByEmail() {
        when(userDao.getUserByEmail("email@gmail.com")).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserByEmail("email@gmail.com");

        assertThat(result).isNotNull();

        verify(userDao).getUserByEmail("email@gmail.com");
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("should throw exception if user does not exist")
    void getUserByIdThrowException() {
        UUID id = UUID.randomUUID();

        doThrow(UserNotFoundException.class).when(userDao).getUserById(id);

        assertThatThrownBy(() -> userService.getUserById(id)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("should get all users")
    void getAllUsers() {
        Page<User> users = new PageImpl<>(List.of(user));

        when(userDao.getAllUsers(null, null, 0, 10)).thenReturn(users);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        Page<UserResponse> result = userService.getAllUsers(null, null, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(userDao).getAllUsers(null, null, 0, 10);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("should update user")
    void updateUser() {
        UUID id = UUID.randomUUID();

        when(userDao.getUserById(id)).thenReturn(user);
        when(userDao.saveUpdatedUser(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUser(id, updateUserRequest);

        assertThat(result).isEqualTo(userResponse);

        verify(userDao).getUserById(id);
        verify(userMapper).updateEntity(updateUserRequest, user);
        verify(userDao).saveUpdatedUser(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("should deactivate user")
    void deactivateUser() {
        UUID id = UUID.randomUUID();

        when(userDao.deactivateUser(id)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.deactivateUser(id);

        assertThat(result).isEqualTo(userResponse);

        verify(userDao).deactivateUser(id);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("should activate user")
    void activateUser() {
        UUID id = UUID.randomUUID();

        when(userDao.activateUser(id)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.activateUser(id);

        assertThat(result).isEqualTo(userResponse);

        verify(userDao).activateUser(id);
        verify(userMapper).toResponse(user);
    }
}