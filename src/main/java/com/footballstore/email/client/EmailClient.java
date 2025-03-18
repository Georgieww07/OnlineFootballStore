package com.footballstore.email.client;

import com.footballstore.email.client.dto.Email;
import com.footballstore.email.client.dto.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "email-svc", url = "http://localhost:8081/api/v1/emails")
public interface EmailClient {

    @PostMapping
    ResponseEntity<Void> sendEmail(@RequestBody EmailRequest emailRequest);

    @GetMapping
    ResponseEntity<List<Email>> getAllEmails();
}
