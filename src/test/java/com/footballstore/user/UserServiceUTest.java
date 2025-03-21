package com.footballstore.user;

import com.footballstore.email.service.EmailService;
import com.footballstore.exception.DomainException;
import com.footballstore.exception.EmailAlreadyExistException;
import com.footballstore.security.AuthenticationMetadata;
import com.footballstore.user.model.User;
import com.footballstore.user.model.UserRole;
import com.footballstore.user.repository.UserRepository;
import com.footballstore.user.service.UserService;
import com.footballstore.web.dto.RegisterRequest;
import com.footballstore.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;


    @Test
    void givenInvalidUserId_whenEditUserInfo_thenExpectDomainException() {
        //Given
        UUID userId = UUID.randomUUID();
        UserEditRequest userEditRequest = UserEditRequest.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(DomainException.class, () -> userService.editUserInfo(userId, userEditRequest));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenValidUserIdAndFullPhoneNumberFromUserEditRequest_whenEditUserInfo_thenSuccessfullyUpdateUserInfo(){
        //Given
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("111111111")
                .build();

        UserEditRequest userEditRequest = UserEditRequest.builder()
                .firstName("Georgi")
                .lastName("Georgiev")
                .phoneNumber("895973441")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //When
        userService.editUserInfo(userId, userEditRequest);

        //Then
        assertEquals("Georgi", user.getFirstName());
        assertEquals("Georgiev", user.getLastName());
        assertEquals("+359895973441", user.getPhoneNumber());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenValidUserIdAndEmptyPhoneNumberFromUserEditRequest_whenEditUserInfo_thenSuccessfullyUpdateUserInfo(){
        //Given
        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("111111111")
                .build();

        UserEditRequest userEditRequest = UserEditRequest.builder()
                .firstName("Georgi")
                .lastName("Georgiev")
                .phoneNumber("")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //When
        userService.editUserInfo(userId, userEditRequest);

        //Then
        assertEquals("Georgi", user.getFirstName());
        assertEquals("Georgiev", user.getLastName());
        assertNull(user.getPhoneNumber());

        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void givenExistingUser_whenRegisterUser_thenEmailAlreadyExistExceptionIsThrown(){
        //Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("123@gmail.com")
                .password("123123")
                .confirmPassword("123123")
                .build();

        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(new User()));

        //When & Then
        assertThrows(EmailAlreadyExistException.class, () -> userService.registerUser(registerRequest));

        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }


    @Test
    void givenHappyPath_whenRegisterUser(){
        //Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("123@gmail.com")
                .password("123123")
                .confirmPassword("123123")
                .build();

        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        //When
        userService.registerUser(registerRequest);

        //Then
        verify(userRepository, times(1)).save(argThat(user ->
                user.getEmail().equals(registerRequest.getEmail()) &&
                        user.getPassword().equals("encodedPassword") &&
                        user.getRole() == UserRole.USER &&
                        user.getCreatedOn() != null
        ));

        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }


    @Test
    void givenUserWithRoleAdmin_whenChangeRole_thenExpectRoleChangedToUser(){
        //Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //When
        userService.changeRole(userId);

        //Then
        assertEquals(UserRole.USER, user.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void givenUserWithRoleUser_whenChangeRole_thenExpectRoleChangedToAdmin(){
        //Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .role(UserRole.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //When
        userService.changeRole(userId);

        //Then
        assertEquals(UserRole.ADMIN, user.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenValidUserId_whenDeleteUser_thenSuccessfullyDeleteUser(){
        //Given
        UUID userId = UUID.randomUUID();

        //When
        userService.deleteUser(userId);

        //Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testGetAllUsers(){
        //Given
        List<User> users = List.of(new User(), new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        //When
        List<User> allUsers = userService.getAllUsers();

        //Then
        assertEquals(users, allUsers);
        assertEquals(users.size(), allUsers.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void givenMissingUserFromDatabase_whenLoadByUsername_thenExceptionIsThrown(){
        //Given
        String email = "123@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //When
        assertThrows(DomainException.class, () -> userService.loadUserByUsername(email));
    }


    @Test
    void givenExistingUser_whenLoadByUsername_thenReturnCorrectAuthenticationMetaData(){
        //Given
        String email = "123@gmail.com";
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .email(email)
                .password("123123")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        //When
        UserDetails userDetails = userService.loadUserByUsername(email);

        //Then
        assertInstanceOf(AuthenticationMetadata.class, userDetails);
        AuthenticationMetadata authenticationMetadata = (AuthenticationMetadata) userDetails;
        assertEquals(userId, authenticationMetadata.getUserId());
        assertEquals(email, authenticationMetadata.getEmail());
        assertEquals("123123", authenticationMetadata.getPassword());
        assertEquals("ROLE_" + UserRole.USER, authenticationMetadata.getAuthorities().iterator().next().getAuthority());
    }
}