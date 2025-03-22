package com.footballstore.product.service;

import com.footballstore.exception.DomainException;
import com.footballstore.product.model.Category;
import com.footballstore.product.model.Product;
import com.footballstore.product.repository.ProductRepository;
import com.footballstore.web.dto.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .imageUrl(productRequest.getImageUrl())
                .brand(productRequest.getBrand())
                .isInStock(productRequest.isInStock())
                .build();

        productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        List<Category> categoryOrder = List.of(Category.BOOTS, Category.BALLS, Category.JERSEYS);

        return productRepository.findAll().stream()
                .sorted(Comparator.comparing(product -> categoryOrder.indexOf(product.getCategory())))
                .toList();
    }

    public List<Product> getFeaturedProducts() {
        Pageable limit = PageRequest.of(0, 3); // Fetch only 3 products
        return productRepository.findRandomInStockProducts(limit);
    }



    public void updateProduct(UUID productId, ProductRequest productRequest) {
        Product product = getProductById(productId);

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setBrand(productRequest.getBrand());
        product.setInStock(productRequest.isInStock());

        productRepository.save(product);

    }

    public void deleteProduct(UUID productId) {
        productRepository.deleteById(productId);
    }

    public Product getProductById(UUID productId) {
        return productRepository.findById(productId).orElseThrow(() -> new DomainException("Product not found."));
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(Category.valueOf(category));
    }

    public List<Product> getSearchedProducts(String name) {
        return productRepository.findByNameIgnoreCaseContaining(name);
    }
}
