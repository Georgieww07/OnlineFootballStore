package com.footballstore.order.service;

import com.footballstore.cart.model.Cart;
import com.footballstore.cart.service.CartService;
import com.footballstore.order.model.Order;
import com.footballstore.order.repository.OrderRepository;
import com.footballstore.order.model.OrderItem;
import com.footballstore.order.repository.OrderItemRepository;
import com.footballstore.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, CartService cartService, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.orderItemRepository = orderItemRepository;
    }


    @Transactional
    public void placeOrder(User user) {

        Cart cart = cartService.getCartByUserId(user.getId());
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty! Cannot place order.");
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .createdOn(LocalDateTime.now())
                .build();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .build())
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);

        order.setItems(orderItems);
        order.setTotalPrice(calculateTotalPrice(order));
        orderRepository.save(order);

        cartService.clearCart(cart.getId());
    }

    private BigDecimal calculateTotalPrice(Order order) {

        return order.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Order> getOrdersByUser(User user) {

        return orderRepository.findAllByUserIdOrderByCreatedOnDesc(user.getId());
    }
}
