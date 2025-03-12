package com.footballstore.product.service;

import com.footballstore.exception.DomainException;
import com.footballstore.product.model.Product;
import com.footballstore.product.repository.ProductRepository;
import com.footballstore.web.dto.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return productRepository.findAll();
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
}
