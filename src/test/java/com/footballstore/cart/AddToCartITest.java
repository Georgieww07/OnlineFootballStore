package com.footballstore.cart;

import com.footballstore.cart.repository.CartItemRepository;
import com.footballstore.cart.service.CartService;
import com.footballstore.email.service.EmailService;
import com.footballstore.product.model.Brand;
import com.footballstore.product.model.Category;
import com.footballstore.product.model.Product;
import com.footballstore.product.repository.ProductRepository;
import com.footballstore.product.service.ProductService;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import com.footballstore.web.dto.ProductRequest;
import com.footballstore.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class AddToCartITest {

    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @MockitoBean
    private EmailService emailService;


    @Test
    void addToCart_happyPath() {
        //Given
        doNothing().when(emailService).sendEmail(any(), any(), any());

        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@gmail.com")
                .password("11111111a")
                .confirmPassword("11111111a")
                .build();

        ProductRequest productRequest = ProductRequest.builder()
                .name("testProduct")
                .description("testDescription")
                .price(BigDecimal.TEN)
                .category(Category.BOOTS)
                .imageUrl("www.google.com")
                .brand(Brand.NIKE)
                .isInStock(true)
                .build();

        userService.registerUser(registerRequest);
        productService.createProduct(productRequest);

        User registeredUser = userService.getAllUsers().get(0);
        registeredUser.setCart(cartService.initCart(registeredUser));
        Product product = productRepository.findByNameIgnoreCaseContainingAndDeletedFalse("testProduct").get(0);

        //When
        cartService.addToCart(registeredUser.getId(), product.getId());

        //Then
        assertNotNull(registeredUser.getCart());
        assertNotNull(registeredUser.getCart().getItems());
        assertEquals(1, cartService.getCartByUserId(registeredUser.getId()).getItems().size());
        assertEquals(product.getId(), cartService.getCartByUserId(registeredUser.getId()).getItems().get(0).getProduct().getId());
        assertEquals(1, cartItemRepository.findAll().size());
    }
}
