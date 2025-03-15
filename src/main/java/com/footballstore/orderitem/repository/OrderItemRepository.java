package com.footballstore.orderitem.repository;

import com.footballstore.orderitem.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    Optional <OrderItem> findByProductId(UUID productId);
}
