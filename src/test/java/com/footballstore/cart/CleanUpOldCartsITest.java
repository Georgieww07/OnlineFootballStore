package com.footballstore.cart;

import com.footballstore.cart.model.Cart;
import com.footballstore.cart.model.CartItem;
import com.footballstore.cart.repository.CartItemRepository;
import com.footballstore.cart.repository.CartRepository;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class CleanUpOldCartsITest {

    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @MockitoBean
    private EmailService emailService;


    @Test
    void cleanUpOldCarts() {
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
        Product product = productRepository.findByNameIgnoreCaseContainingAndDeletedFalse("testProduct").get(0);

        Cart cart = Cart.builder()
                .user(registeredUser)
                .lastUpdated(LocalDateTime.now().minusDays(8))
                .build();
        cartRepository.save(cart);

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(1)
                .build();
        cartItemRepository.save(cartItem);
        cart.setItems(List.of(cartItem));

        //When
        cartService.cleanUpOldCarts();

        //Then
        Cart registeredUserCart = cartService.getCartByUserId(registeredUser.getId());
        assertEquals(0, registeredUserCart.getItems().size());
    }
}
