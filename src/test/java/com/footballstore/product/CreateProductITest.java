package com.footballstore.product;

import com.footballstore.product.model.Brand;
import com.footballstore.product.model.Category;
import com.footballstore.product.model.Product;
import com.footballstore.product.repository.ProductRepository;
import com.footballstore.product.service.ProductService;
import com.footballstore.web.dto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class CreateProductITest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;


    @Test
    void createProduct() {
        //Given
        ProductRequest productRequest = ProductRequest.builder()
                .name("testProduct")
                .description("testDescription")
                .price(BigDecimal.TEN)
                .category(Category.BOOTS)
                .imageUrl("www.google.com")
                .brand(Brand.NIKE)
                .isInStock(true)
                .build();

        //When
        productService.createProduct(productRequest);

        //Then
        Product createdProduct = productRepository.findByNameIgnoreCaseContainingAndDeletedFalse(productRequest.getName()).get(0);

        assertNotNull(createdProduct);
        assertEquals(productRequest.getName(), createdProduct.getName());
        assertEquals(productRequest.getDescription(), createdProduct.getDescription());
        assertEquals(0, productRequest.getPrice().compareTo(createdProduct.getPrice()));
        assertEquals(productRequest.getCategory(), createdProduct.getCategory());
        assertEquals(productRequest.getImageUrl(), createdProduct.getImageUrl());
        assertEquals(productRequest.getBrand(), createdProduct.getBrand());
        assertTrue(createdProduct.isInStock());
        assertFalse(createdProduct.isDeleted());
    }
}
