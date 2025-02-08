package com.footballstore.cart.model;

import com.footballstore.cartitem.model.CartItem;
import com.footballstore.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private User user;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "cart")
    List<CartItem> items = new ArrayList<>();
}
