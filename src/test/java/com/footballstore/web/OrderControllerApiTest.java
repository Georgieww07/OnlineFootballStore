package com.footballstore.web;

import com.footballstore.order.service.OrderService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getUnauthenticatedRequestToOrdersEndpoint_shouldRedirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = get("/orders");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(userService, never()).getUserById(any());
        verify(orderService, never()).getOrdersByUser(any());
    }

    @Test
    void getAuthenticatedRequestToOrdersEndpoint_shouldReturnOrdersView() throws Exception {

        when(userService.getUserById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = get("/orders")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("user"));

        verify(userService, times(1)).getUserById(userId);
        verify(orderService, times(1)).getOrdersByUser(any());
    }

    @Test
    void postUnauthenticatedRequestToOrdersEndpoint_shouldRedirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = post("/orders")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(userService, never()).getUserById(any());
        verify(orderService, never()).placeOrder(any());
    }

    @Test
    void postAuthenticatedRequestToOrdersEndpoint_shouldRedirectToOrders() throws Exception {

        when(userService.getUserById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = post("/orders")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(userService, times(1)).getUserById(userId);
        verify(orderService, times(1)).placeOrder(any());
    }
}
