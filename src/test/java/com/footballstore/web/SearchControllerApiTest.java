package com.footballstore.web;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(SearchController.class)
public class SearchControllerApiTest {

    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getUnauthenticatedRequestToSearchEndpoint_shouldRedirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = get("/search").param("query", "");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(productService, never()).getSearchedProducts(any());
        verify(userService, never()).getUserById(any());
    }

    @Test
    void getAuthenticatedRequestToSearchEndpoint_shouldReturnSearchResultsView() throws Exception {

        when(userService.getUserById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "asd@gmail.com", "11111111a", UserRole.USER);
        MockHttpServletRequestBuilder request = get("/search")
                .param("query", "")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attributeExists("searchResults"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("query"));

        verify(productService, times(1)).getSearchedProducts(any());
        verify(userService, times(1)).getUserById(any());
    }
}