package com.grits.userservice.dao;

import com.grits.userservice.entity.User;
import com.grits.userservice.exception.UserAlreadyExistsException;
import com.grits.userservice.exception.UserNotFoundException;
import com.grits.userservice.repository.UserRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDaoTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDao userDao;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setKeycloakUserId(UUID.randomUUID());
        user.setActive(true);
        user.setName("name");
        user.setSurname("surname");
        user.setEmail("email@gmail.com");
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("should save a new user")
    void save() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User result = userDao.save(user);

        assertThat(result).isEqualTo(user);

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should throw exception if user exists")
    void saveWithException() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userDao.save(user)).isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).existsByEmail(user.getEmail());
    }

    @Test
    @DisplayName("should save updated user")
    void saveUpdatedUser() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userDao.saveUpdatedUser(user);

        assertThat(result).isEqualTo(user);

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should get user by id")
    void getUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userDao.getUserById(userId);

        assertThat(result).isEqualTo(user);

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("should get user by email")
    void getUserByEmail() {
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.of(user));

        User result = userDao.getUserByEmail("email@gmail.com");

        assertThat(result).isEqualTo(user);

        verify(userRepository).findByEmail("email@gmail.com");
    }

    @Test
    @DisplayName("should throw exception if user does not exist")
    void getUserByIdWithException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDao.getUserById(userId)).isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("should throw exception if user's email does not exist")
    void getUserByEmailWithException() {
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDao.getUserByEmail("email@gmail.com")).isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByEmail("email@gmail.com");
    }

    @Test
    @DisplayName("should deactivate user")
    void deactivateUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userDao.deactivateUser(userId);

        assertThat(result.isActive()).isFalse();

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should activate user")
    void activateUser() {
        user.setActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userDao.activateUser(userId);

        assertThat(result.isActive()).isTrue();

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should get all users")
    void getAllUsers() {
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userDao.getAllUsers(null, null, 0, 10);

        assertThat(result).isEqualTo(userPage);
    }
}