package com.grits.userservice.dao;

import com.grits.userservice.entity.User;
import com.grits.userservice.exception.UserAlreadyExistsException;
import com.grits.userservice.exception.UserNotFoundException;
import com.grits.userservice.repository.UserRepository;
import com.grits.userservice.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDao {

    private final UserRepository userRepository;

    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail());
        }
        return userRepository.save(user);
    }

    public User saveUpdatedUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(email)
        );
    }

    public User deactivateUser(UUID id) {
        User user = getUserById(id);
        user.setActive(false);
        return userRepository.save(user);
    }

    public User activateUser(UUID id) {
        User user = getUserById(id);
        user.setActive(true);
        return userRepository.save(user);
    }

    public Page<User> getAllUsers(String name, String surname, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("surname").ascending());
        Specification<User> specification = Specification.allOf(
                UserSpecification.hasName(name),
                UserSpecification.hasSurname(surname)
        );
        return userRepository.findAll(specification, pageable);
    }
}
