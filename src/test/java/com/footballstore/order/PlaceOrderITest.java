package com.footballstore.order;

import com.footballstore.cart.service.CartService;
import com.footballstore.email.service.EmailService;
import com.footballstore.order.repository.OrderItemRepository;
import com.footballstore.order.service.OrderService;
import com.footballstore.product.model.Brand;
import com.footballstore.product.model.Category;
import com.footballstore.product.model.Product;
import com.footballstore.product.repository.ProductRepository;
import com.footballstore.product.service.ProductService;
import com.footballstore.user.model.User;
import com.footballstore.user.repository.UserRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class PlaceOrderITest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @MockitoBean
    private EmailService emailService;


    @Test
    void placeOrder_happyPath(){
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

        // Create product
        productService.createProduct(productRequest);
        List<Product> products = productRepository.findByNameIgnoreCaseContainingAndDeletedFalse(productRequest.getName());
        Product product = products.get(0);

        // Register user (email sending is mocked)
        userService.registerUser(registerRequest);
        Optional<User> optionalUser = userRepository.findByEmail("test@gmail.com");
        User registeredUser = optionalUser.get();

        // Initialize cart and add product
        registeredUser.setCart(cartService.initCart(registeredUser));
        cartService.addToCart(registeredUser.getId(), product.getId());

        //When
        orderService.placeOrder(registeredUser);

        //Then
        assertEquals(1, orderService.getOrdersByUser(registeredUser).size());
        assertEquals(product.getId(), orderService.getOrdersByUser(registeredUser).get(0).getItems().get(0).getProduct().getId());
        assertEquals(registeredUser.getId(), orderService.getOrdersByUser(registeredUser).get(0).getUser().getId());
        assertEquals(1, orderItemRepository.findAll().size());
    }
}
