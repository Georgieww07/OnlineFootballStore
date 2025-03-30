package com.footballstore;

import com.footballstore.cart.model.Cart;
import com.footballstore.order.model.Order;
import com.footballstore.user.model.User;
import com.footballstore.user.model.UserRole;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TestBuilder {

    public static User aRandomUser() {
        User user =  User.builder()
                .id(UUID.randomUUID())
                .email("asd@gmail.com")
                .password("11111111a")
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .build();

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .createdOn(LocalDateTime.now())
                .totalPrice(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .user(user)
                .lastUpdated(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        user.setOrders(List.of(order));
        user.setCart(cart);

        return user;
    }
}
