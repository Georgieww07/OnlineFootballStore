package com.footballstore.cart.service;

import com.footballstore.cart.model.Cart;
import com.footballstore.cart.repository.CartRepository;
import com.footballstore.cart.model.CartItem;
import com.footballstore.cart.repository.CartItemRepository;
import com.footballstore.exception.DomainException;
import com.footballstore.product.model.Product;
import com.footballstore.product.service.ProductService;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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

        cart.setLastUpdated(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Transactional
    public void deleteCartItem(UUID productId, User user) {

        List<CartItem> cartItems = cartItemRepository.findByProductId(productId);
        if (!cartItems.isEmpty()) {

            List<CartItem> cartItemsByCurrentUser = cartItems.stream()
                    .filter(cartItem -> cartItem.getCart().getId().equals(user.getCart().getId()))
                    .toList();

            Cart cart = user.getCart();
            cart.getItems().removeAll(cartItemsByCurrentUser);
            cart.setLastUpdated(LocalDateTime.now());

            cartRepository.save(cart);
            cartItemRepository.deleteAll(cartItemsByCurrentUser);
        }
    }

    @Transactional
    public void deleteCartItemFullyFromDb(UUID productId) {

        List<CartItem> cartItems = cartItemRepository.findByProductId(productId);
        if (!cartItems.isEmpty()) {

            cartItems.forEach(cartItem -> {
                Cart cart = cartItem.getCart();
                cart.getItems().remove(cartItem);
                cart.setLastUpdated(LocalDateTime.now());

                cartRepository.save(cart);
                cartItemRepository.delete(cartItem);
            });
        }
    }

    public BigDecimal getCartTotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Cart initCart(User user){
        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .lastUpdated(LocalDateTime.now())
                .build();

        return cartRepository.save(cart);
    }

    public Cart getCartByUserId(UUID userId) {
        Optional<Cart> cart = cartRepository.findByUserId(userId);
        return cart.orElseGet(() -> initCart(userService.getUserById(userId)));

    }

    @Transactional
    public void clearCart(UUID cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new DomainException("Cart with cart id [%s] not found!".formatted(cartId)));

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();

        cart.setLastUpdated(LocalDateTime.now());

        cartRepository.save(cart);
    }

    @Transactional
    public void cleanUpOldCarts() {

        LocalDateTime expirationTime = LocalDateTime.now().minusDays(7);

        List<Cart> oldCarts = cartRepository.findByLastUpdatedBefore(expirationTime);

        oldCarts.forEach(cart -> clearCart(cart.getId()));

        log.info("[%d] abandoned carts cleared.".formatted(oldCarts.size()));
    }
}
