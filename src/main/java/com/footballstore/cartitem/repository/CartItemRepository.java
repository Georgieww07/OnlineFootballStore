package com.footballstore.cartitem.repository;

import com.footballstore.cartitem.model.CartItem;
import com.footballstore.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    CartItem findByProductId(UUID productId);
}
