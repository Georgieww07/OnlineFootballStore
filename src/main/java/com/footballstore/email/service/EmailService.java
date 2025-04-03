package com.footballstore.email.service;

import com.footballstore.email.client.EmailClient;
import com.footballstore.email.client.dto.Email;
import com.footballstore.email.client.dto.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class EmailService {

    private final EmailClient emailClient;

    @Autowired
    public EmailService(EmailClient emailClient) {
        this.emailClient = emailClient;
    }


    public void sendEmail(String to, String subject, String body) {

        EmailRequest emailRequest = EmailRequest.builder()
                .receiver(to)
                .subject(subject)
                .body(body)
                .build();

        ResponseEntity<Void> httpResponse = emailClient.sendEmail(emailRequest);

        if (!httpResponse.getStatusCode().is2xxSuccessful()) {
            log.error("[Feign call to email-svc failed] Cannot send email to [%s]".formatted(to));
        }
    }

    public List<Email> getEmails() {

        return Objects.requireNonNull(emailClient.getAllEmails().getBody())
                .stream()
                .toList();
    }
}
