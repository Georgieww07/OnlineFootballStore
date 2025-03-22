package com.footballstore.user.service;

import com.footballstore.email.service.EmailService;
import com.footballstore.exception.DomainException;
import com.footballstore.exception.EmailAlreadyExistException;
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
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void registerUser(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(registerRequest.getEmail());

        if (optionalUser.isPresent()) {
            throw new EmailAlreadyExistException("User with email [%s] already exists!".formatted(registerRequest.getEmail()));
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String emailBody = "Welcome to our community. Shop easily top-quality products.";
        emailService.sendEmail(user.getEmail(), "Sending email for registering", emailBody);

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
        return userRepository.findAllByOrderByCreatedOnDesc();
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DomainException("User not found!"));
    }

    public void changeRole(UUID userId) {
        User user = getUserById(userId);

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
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

    public void createAdminIfNotExist(){
        Optional<User> optionalUser = userRepository.findByEmail("onlinefootballstoreofficial@gmail.com");

        if (optionalUser.isEmpty()) {
            User adminUser = User.builder()
                    .email("onlinefootballstoreofficial@gmail.com")
                    .password(passwordEncoder.encode("footballstore123"))
                    .role(UserRole.ADMIN)
                    .firstName("ADMIN")
                    .lastName("ADMIN")
                    .createdOn(LocalDateTime.now())
                    .build();

            userRepository.save(adminUser);

            log.info("Successfully created admin with email [%s].".formatted(adminUser.getEmail()));
        }
    }
}
