package com.footballstore.user.service;

import com.footballstore.exception.DomainException;
import com.footballstore.user.model.User;
import com.footballstore.user.model.UserRole;
import com.footballstore.user.repository.UserRepository;
import com.footballstore.web.dto.LoginRequest;
import com.footballstore.web.dto.RegisterRequest;
import com.footballstore.web.dto.UserEditRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    public User loginUser(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            throw new DomainException("Wrong email address or password!");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new DomainException("Wrong email address or password!");
        }

        return user;
    }

    public void editUserInfo(UUID userId, UserEditRequest userEditRequest) {
        User user = getUserById(userId);

        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setPhoneNumber(userEditRequest.getPhoneNumber());

        userRepository.save(user);

    }

    private User initializeUser(RegisterRequest registerRequest) {
        return User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.CUSTOMER)
                .build();
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DomainException("User not found!"));
    }
}
