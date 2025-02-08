package com.footballstore.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    @Email(message = "Invalid email constraints.")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters long.")
    private String password;
}
