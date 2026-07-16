package com.grits.userservice.model.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "Name is required")
    private String name;


    @NotBlank(message = "Surname is required")
    private String surname;


    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
