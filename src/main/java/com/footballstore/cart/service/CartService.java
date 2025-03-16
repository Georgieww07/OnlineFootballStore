package com.footballstore.cart.service;

import com.footballstore.cart.model.Cart;
import com.footballstore.cart.repository.CartRepository;
import com.footballstore.cartitem.model.CartItem;
import com.footballstore.cartitem.repository.CartItemRepository;
import com.footballstore.exception.DomainException;
import com.footballstore.product.model.Product;
import com.footballstore.product.service.ProductService;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        cart.setLastUpdated(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Transactional
    public void deleteCartItem(UUID productId) {
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
                .lastUpdated(LocalDateTime.now())
                .build();

        return cartRepository.save(cart);
    }

    public Cart getCartByUserId(UUID userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart with user [%s] not found!".formatted(userId)));
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
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(1);

        List<Cart> oldCarts = cartRepository.findByLastUpdatedBefore(expirationTime);

        cartRepository.deleteAll(oldCarts);
        System.out.println(oldCarts.size() + " abandoned carts deleted.");

    }
}
