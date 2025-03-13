package com.footballstore.cartitem.model;

import com.footballstore.cart.model.Cart;
import com.footballstore.product.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Product product;

    @Column(nullable = false)
    private int quantity;

}
