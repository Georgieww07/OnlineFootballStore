package com.footballstore.user.service;

import com.footballstore.exception.DomainException;
import com.footballstore.user.model.User;
import com.footballstore.user.model.UserRole;
import com.footballstore.user.repository.UserRepository;
import com.footballstore.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(registerRequest.getEmail());

        if (optionalUser.isPresent()) {
            throw new DomainException("User with email [%s] already exists!".formatted(registerRequest.getEmail()));
        }

        log.info("Successfully registered user with email [%s].".formatted(registerRequest.getEmail()));

        return userRepository.save(initializeUser(registerRequest));
    }

    private User initializeUser(RegisterRequest registerRequest) {
        return User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.CUSTOMER)
                .build();
    }
}
