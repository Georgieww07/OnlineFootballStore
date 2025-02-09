package com.footballstore.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class RegisterRequest {

    @Email(message = "Invalid email constraints.")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must contain at least one letter and one number.")
    private String password;
}
