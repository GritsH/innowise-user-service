package com.grits.userservice.model.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;

    private String name;

    private String surname;

    private LocalDate birthDate;

    private String email;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
