package com.footballstore.product.repository;

import com.footballstore.product.model.Category;
import com.footballstore.product.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p FROM Product p WHERE p.isInStock = true ORDER BY FUNCTION('RAND')")
    List<Product> findRandomInStockProducts(Pageable pageable);

    List<Product> findByCategory(Category category);

    List<Product> findByNameIgnoreCaseContaining(String name);

}
