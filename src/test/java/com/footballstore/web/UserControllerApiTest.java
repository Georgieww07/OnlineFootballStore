package com.footballstore.web;

import com.footballstore.security.AuthenticationMetadata;
import com.footballstore.user.model.UserRole;
import com.footballstore.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static com.footballstore.TestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getUnauthenticatedRequestToUsersEndpoint_shouldRedirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = get("/users");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(userService, never()).getUserById(any());
        verify(userService, never()).getAllUsers();
    }

    @Test
    void getUnauthorizedRequestToUsersEndpoint_shouldReturn404AndNotFoundView() throws Exception {

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = get("/users")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));

        verify(userService, never()).getUserById(any());
        verify(userService, never()).getAllUsers();
    }

    @Test
    void getAuthorizedRequestToUsersEndpoint_shouldReturnUsersView() throws Exception {

        when(userService.getUserById(any())).thenReturn(aRandomUser());

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "asd@gmail.com", "11111111a", UserRole.ADMIN);
        MockHttpServletRequestBuilder request = get("/users")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("users"));

        verify(userService, times(1)).getUserById(any());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUnauthenticatedRequestToUserProfileEndpoint_shouldRedirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = get("/users/{id}/profile", UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(userService, never()).getUserById(any());
    }

    @Test
    void getAuthenticatedRequestToUserProfileEndpoint_shouldReturnProfileView() throws Exception {

        when(userService.getUserById(any())).thenReturn(aRandomUser());

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = get("/users/{id}/profile", UUID.randomUUID())
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userEditRequest"));

        verify(userService, times(1)).getUserById(any());
    }

    @Test
    void putUnauthenticatedRequestToUserProfileEndpoint_shouldRedirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = put("/users/{id}/profile", UUID.randomUUID())
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(userService, never()).getUserById(any());
        verify(userService, never()).editUserInfo(any(), any());
    }

    @Test
    void putAuthenticatedRequestToUserProfileEndpointWithInvalidUserEditRequest_shouldReturnProfileView() throws Exception {

        when(userService.getUserById(any())).thenReturn(aRandomUser());

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = put("/users/{id}/profile", UUID.randomUUID())
                .formField("firstName", "OldFirstName")
                .formField("lastName", "OldLastName")
                .formField("phoneNumber", "123")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userEditRequest"));

        verify(userService, times(1)).getUserById(any());
        verify(userService, never()).editUserInfo(any(), any());
    }

    @Test
    void putAuthenticatedRequestToUserProfileEndpointHappyPath__shouldRedirectToHome() throws Exception {

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = put("/users/{id}/profile", UUID.randomUUID())
                .formField("firstName", "OldFirstName")
                .formField("lastName", "OldLastName")
                .formField("phoneNumber", "895435678")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService, never()).getUserById(any());
        verify(userService, times(1)).editUserInfo(any(), any());
    }

    @Test
    void putUnauthorizedRequestToUserRoleEndpoint_shouldReturn404AndNotFoundView() throws Exception {

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = put("/users/{id}/role", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));

        verify(userService, never()).changeRole(any());
    }

    @Test
    void putAuthorizedRequestToUserRoleEndpoint_shouldRedirectToUsers() throws Exception {

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "asd@gmail.com", "11111111a", UserRole.ADMIN);
        MockHttpServletRequestBuilder request = put("/users/{id}/role", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService, times(1)).changeRole(any());
    }

    @Test
    void deleteUnauthorizedRequestToUsers_shouldReturn404AndNotFoundView() throws Exception {

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = delete("/users/{id}", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));

        verify(userService, never()).deleteUser(any());
    }

    @Test
    void deleteAuthorizedRequestToUsers_shouldRedirectToUsers() throws Exception {

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "asd@gmail.com", "11111111a", UserRole.ADMIN);
        MockHttpServletRequestBuilder request = delete("/users/{id}", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService, times(1)).deleteUser(any());
    }
}