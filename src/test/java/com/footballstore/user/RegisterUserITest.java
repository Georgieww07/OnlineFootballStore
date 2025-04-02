package com.footballstore.user;

import com.footballstore.email.service.EmailService;
import com.footballstore.user.model.User;
import com.footballstore.user.model.UserRole;
import com.footballstore.user.repository.UserRepository;
import com.footballstore.user.service.UserService;
import com.footballstore.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class RegisterUserITest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @MockitoBean
    private EmailService emailService;


    @Test
    void registerUser_happyPath(){
        //Given
        doNothing().when(emailService).sendEmail(any(), any(), any());

        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@gmail.com")
                .password("11111111a")
                .confirmPassword("11111111a")
                .build();

        //When
        userService.registerUser(registerRequest);

        //Then
        Optional<User> optionalUser = userRepository.findByEmail("test@gmail.com");

        assertNotNull(optionalUser);
        User registeredUser = optionalUser.get();
        assertThat(registeredUser.getEmail()).isEqualTo("test@gmail.com");
        assertThat(registeredUser.getPassword()).isNotEqualTo("11111111a");
        assertEquals(UserRole.USER, registeredUser.getRole());
    }
}
