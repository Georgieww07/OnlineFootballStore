package com.footballstore.user.service;

import com.footballstore.exception.DomainException;
import com.footballstore.security.AuthenticationMetadata;
import com.footballstore.user.model.User;
import com.footballstore.user.model.UserRole;
import com.footballstore.user.repository.UserRepository;
import com.footballstore.web.dto.RegisterRequest;
import com.footballstore.web.dto.UserEditRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(registerRequest.getEmail());

        if (optionalUser.isPresent()) {
            throw new DomainException("User with email [%s] already exists!".formatted(registerRequest.getEmail()));
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.CUSTOMER)
                .createdOn(LocalDateTime.now())
                .build();

        userRepository.save(user);

        log.info("Successfully registered user with email [%s].".formatted(registerRequest.getEmail()));
    }

    public void editUserInfo(UUID userId, UserEditRequest userEditRequest) {
        User user = getUserById(userId);

        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        if (!userEditRequest.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber("+359" + userEditRequest.getPhoneNumber());
        } else {
            user.setPhoneNumber(null);
        }

        userRepository.save(user);

        log.info("Successfully edited user with email [%s].".formatted(user.getEmail()));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DomainException("User not found!"));
    }

    public void changeRole(UUID userId) {
        User user = getUserById(userId);

        if (user.getRole() == UserRole.CUSTOMER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.CUSTOMER);
        }

        userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException("User with email [%s] does not exist!".formatted(email)));

        return new AuthenticationMetadata(user.getId(), user.getEmail(), user.getPassword(), user.getRole());
    }
}
