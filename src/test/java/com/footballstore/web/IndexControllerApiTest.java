package com.footballstore.web;

import com.footballstore.exception.EmailAlreadyExistException;
import com.footballstore.product.service.ProductService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getRequestToIndexEndpoint_shouldReturnIndexView() throws Exception {

        MockHttpServletRequestBuilder request = get("/");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getRequestToRegisterEndpoint_shouldReturnRegisterView() throws Exception {

        MockHttpServletRequestBuilder request = get("/register");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void getRequestToLoginEndpoint_shouldReturnLoginView() throws Exception {

        MockHttpServletRequestBuilder request = get("/login");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void getRequestToLoginEndpointWithErrorParam_shouldReturnLoginViewAndErrorMessageAttribute() throws Exception {

        MockHttpServletRequestBuilder request = get("/login").param("error", "");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void postRequestToRegisterEndpoint_happyPath() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .formField("email", "asd@gmail.com")
                .formField("password", "11111111a")
                .formField("confirmPassword", "11111111a")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).registerUser(any());
    }

    @Test
    void postRequestToRegisterEndpointWithInvalidData_shouldReturnRegisterView() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .formField("email", "asd")
                .formField("password", "123")
                .formField("confirmPassword", "123")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).registerUser(any());
    }

    @Test
    void postRequestToRegisterEndpointWithNotEqualPasswordAndConfirmPasswordFields_shouldReturnRegisterViewAndErrorMessageAttribute() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .formField("email", "asd@gmail.com")
                .formField("password", "11111111a")
                .formField("confirmPassword", "22222222a")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(userService, never()).registerUser(any());
    }

    @Test
    void postRequestToRegisterEndpointWhenEmailAlreadyExist_shouldRedirectToRegisterWithFlashAttribute() throws Exception {

        doThrow(new EmailAlreadyExistException("Email already exists"))
                .when(userService)
                .registerUser(any());

        MockHttpServletRequestBuilder request = post("/register")
                .formField("email", "asd@gmail.com")
                .formField("password", "11111111a")
                .formField("confirmPassword", "11111111a")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("emailAlreadyExistMessage"));

        verify(userService, times(1)).registerUser(any());
    }

    @Test
    void getUnauthenticatedRequestToHomeEndpoint_shouldRedirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = get("/home");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(userService, never()).getUserById(any());
        verify(productService, never()).getFeaturedProducts();
    }

    @Test
    void getAuthenticatedRequestToHomeEndpoint_shouldReturnHomeView() throws Exception {

        when(userService.getUserById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = get("/home").with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("featuredProducts"));

        verify(userService, times(1)).getUserById(userId);
        verify(productService, times(1)).getFeaturedProducts();
    }
}