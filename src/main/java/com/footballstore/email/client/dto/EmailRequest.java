package com.footballstore.email.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRequest {

    private String receiver;

    private String subject;

    private String body;
}
