package com.footballstore.cart.service;

import com.footballstore.cart.model.Cart;
import com.footballstore.cart.repository.CartRepository;
import com.footballstore.cartitem.model.CartItem;
import com.footballstore.cartitem.repository.CartItemRepository;
import com.footballstore.product.model.Product;
import com.footballstore.product.service.ProductService;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {
    private final UserService userService;
    private final ProductService productService;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Autowired
    public CartService(UserService userService, ProductService productService, CartItemRepository cartItemRepository, CartRepository cartRepository) {
        this.userService = userService;
        this.productService = productService;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
    }


    @Transactional
    public void addToCart(UUID userId, UUID productId) {
        User user = userService.getUserById(userId);

        if (user.getCart() == null) {
            Cart cart = initCart(user);

            user.setCart(cart);
        }

        Cart cart = user.getCart();

        Product product = productService.getProductById(productId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);  // Update the quantity
            cartItemRepository.save(item);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(1)
                    .build();
            cartItemRepository.save(cartItem);
            cart.getItems().add(cartItem);
        }

        cartRepository.save(cart);
    }

    public void deleteCartItem(UUID productId) {
        CartItem byProductId = cartItemRepository.findByProductId(productId);

        if (byProductId != null) {
            cartItemRepository.delete(byProductId);
        }
    }

    public List<CartItem> getCartItems() {
        return cartItemRepository.findAll();
    }

    public BigDecimal getCartTotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Cart initCart(User user){
        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<CartItem>())
                .build();

        return cartRepository.save(cart);
    }
}
