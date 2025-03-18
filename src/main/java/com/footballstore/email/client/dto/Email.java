package com.footballstore.email.client.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Email {

    private String subject;

    private LocalDateTime createdOn;

    private String status;

    private String receiver;
}
