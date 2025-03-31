package com.footballstore.web;

import com.footballstore.cart.service.CartService;
import com.footballstore.security.AuthenticationMetadata;
import com.footballstore.user.model.User;
import com.footballstore.user.model.UserRole;
import com.footballstore.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.UUID;

import static com.footballstore.TestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private CartService cartService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getUnauthenticatedRequestToCartEndpoint_shouldRedirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = get("/cart");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(userService, never()).getUserById(any());
        verify(cartService, never()).getCartByUserId(any());
        verify(cartService, never()).getCartTotal(any());
    }

    @Test
    void getAuthenticatedRequestToCartEndpoint_shouldReturnCartView() throws Exception {

        UUID userId = UUID.randomUUID();
        User testUser = aRandomUser();
        testUser.setId(userId);

        when(userService.getUserById(userId)).thenReturn(testUser);
        when(cartService.getCartByUserId(userId)).thenReturn(testUser.getCart());
        when(cartService.getCartTotal(any())).thenReturn(BigDecimal.ZERO);

        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = get("/cart")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("cartTotal"));

        verify(userService, times(1)).getUserById(userId);
        verify(cartService, times(1)).getCartByUserId(userId);
        verify(cartService, times(1)).getCartTotal(any());
    }

    @Test
    void postUnauthenticatedRequestToAddToCart_shouldRedirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = post("/cart/add").param("productId", "")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(userService, never()).getUserById(any());
        verify(cartService, never()).addToCart(any(), any());
    }

    @Test
    void postAuthenticatedRequestToAddToCart_shouldRedirectToProducts() throws Exception {

        when(userService.getUserById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = post("/cart/add").param("productId", UUID.randomUUID().toString())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(userService, times(1)).getUserById(any());
        verify(cartService, times(1)).addToCart(any(), any());
    }

    @Test
    void deleteUnauthenticatedRequestToRemoveCartItem_shouldRedirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = delete("/cart/{id}", UUID.randomUUID())
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(cartService, never()).deleteCartItem(any());
    }

    @Test
    void deleteAuthenticatedRequestToRemoveCartItem_shouldRedirectToCart() throws Exception {

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = delete("/cart/{id}", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService, times(1)).deleteCartItem(any());
    }
}