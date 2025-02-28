package com.footballstore.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEditRequest {

    @Size(max = 30, message = "First name shouldn't contain more than 30 letters.")
    private String firstName;

    @Size(max = 30, message = "Last name shouldn't contain more than 30 letters.")
    private String lastName;

    @Pattern(regexp = "^[0-9]{9}$", message = "Invalid phone number constraints.")
    private String phoneNumber;
}
