package com.footballstore.product;

import com.footballstore.exception.DomainException;
import com.footballstore.product.model.Brand;
import com.footballstore.product.model.Category;
import com.footballstore.product.model.Product;
import com.footballstore.product.repository.ProductRepository;
import com.footballstore.product.service.ProductService;
import com.footballstore.web.dto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceUTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;


    @Test
    void givenProductRequestDto_whenCreateProduct_thenSuccessfullyCreatedNewProduct() {
        //Given
        ProductRequest productRequest = ProductRequest.builder()
                .name("Nike boots")
                .description("Amazing new product from our collection")
                .price(BigDecimal.TEN)
                .category(Category.BOOTS)
                .imageUrl("http://example.com/football.jpg")
                .brand(Brand.NIKE)
                .isInStock(true)
                .build();

        //When
        productService.createProduct(productRequest);

        //Then
        verify(productRepository, times(1)).save(argThat(product ->
                product.getName().equals(productRequest.getName())
                && product.getDescription().equals(productRequest.getDescription())
                && product.getPrice().equals(productRequest.getPrice())
                && product.getCategory().equals(productRequest.getCategory())
                && product.getImageUrl().equals(productRequest.getImageUrl())
                && product.getBrand().equals(productRequest.getBrand())
                && product.isInStock() == productRequest.isInStock()
        ));
    }

    @Test
    void testGetAllProducts() {
        //Given
        List<Product> products = List.of(
                Product.builder().category(Category.JERSEYS).build(),
                Product.builder().category(Category.BALLS).build(),
                Product.builder().category(Category.BOOTS).build()
        );

        when(productRepository.findAll()).thenReturn(products);

        //When
        List<Product> allProducts = productService.getAllProducts();

        //Then
        assertNotNull(allProducts);
        assertEquals(Category.BOOTS, allProducts.get(0).getCategory());
        assertEquals(Category.BALLS, allProducts.get(1).getCategory());
        assertEquals(Category.JERSEYS, allProducts.get(2).getCategory());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetFeaturedProducts() {
        //Given
        Pageable limit = PageRequest.of(0, 3);
        List<Product> products = List.of(
                Product.builder().isInStock(true).build(),
                Product.builder().isInStock(true).build(),
                Product.builder().isInStock(true).build()
        );

        when(productRepository.findRandomInStockProducts(limit)).thenReturn(products);

        //When
        List<Product> featuredProducts = productService.getFeaturedProducts();

        //Then
        assertNotNull(featuredProducts);
        assertEquals(3, featuredProducts.size());
        assertTrue(featuredProducts.get(0).isInStock());
        assertTrue(featuredProducts.get(1).isInStock());
        assertTrue(featuredProducts.get(2).isInStock());

        verify(productRepository, times(1)).findRandomInStockProducts(limit);
    }

    @Test
    void givenInvalidProductId_whenUpdateProduct_thenExceptionIsThrown() {
        //Given
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(DomainException.class, () -> productService.updateProduct(productId, ProductRequest.builder().build()));

        verify(productRepository, never()).save(any());
    }

    @Test
    void givenValidProductId_whenUpdateProduct_thenSuccessfullyUpdatedProduct() {
        //Given
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .id(productId)
                .name("Nike boots")
                .description("Amazing new product from our collection")
                .price(BigDecimal.TEN)
                .category(Category.BOOTS)
                .imageUrl("http://example.com/football.jpg")
                .brand(Brand.NIKE)
                .isInStock(true)
                .build();

        ProductRequest productRequest = ProductRequest.builder()
                .name("Adidas BALL")
                .description("Updated description")
                .price(BigDecimal.ZERO)
                .category(Category.BALLS)
                .imageUrl("http://example.com/ball.jpg")
                .brand(Brand.ADIDAS)
                .isInStock(false)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        //When
        productService.updateProduct(productId, productRequest);

        //Then
        assertEquals("Adidas BALL", product.getName());
        assertEquals("Updated description", product.getDescription());
        assertEquals(BigDecimal.ZERO, product.getPrice());
        assertEquals(Category.BALLS, product.getCategory());
        assertEquals("http://example.com/ball.jpg", product.getImageUrl());
        assertEquals(Brand.ADIDAS, product.getBrand());
        assertFalse(product.isInStock());

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void givenValidProductId_whenDeleteProduct_thenSuccessfullyDeletedProduct() {
        //Given
        UUID productId = UUID.randomUUID();

        //When
        productService.deleteProduct(productId);

        //Then
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void givenCategory_whenGetProductsByCategory_thenReturnListOfProductsWithThatCategory() {
        //Given
       String category = Category.BOOTS.toString();
        List<Product> products = List.of(
                Product.builder().category(Category.BOOTS).build(),
                Product.builder().category(Category.BOOTS).build(),
                Product.builder().category(Category.BOOTS).build()
        );

        when(productRepository.findByCategory(Category.valueOf(category))).thenReturn(products);

        //When
        List<Product> productsByCategory = productService.getProductsByCategory(category);

        //Then
        assertNotNull(productsByCategory);
        assertEquals(3, productsByCategory.size());
        assertEquals(Category.BOOTS, productsByCategory.get(0).getCategory());
        assertEquals(Category.BOOTS, productsByCategory.get(1).getCategory());
        assertEquals(Category.BOOTS, productsByCategory.get(2).getCategory());

        verify(productRepository, times(1)).findByCategory(Category.valueOf(category));
    }

    @Test
    void givenProductName_whenGetSearchedProducts_thenReturnListOfProductsWithThatName() {
        //Given
        String name = "Amazing football boots";
        List<Product> products = List.of(
                Product.builder().name(name).build(),
                Product.builder().name(name).build()
        );

        when(productRepository.findByNameIgnoreCaseContaining(name)).thenReturn(products);

        //When
        List<Product> searchedProducts = productService.getSearchedProducts(name);

        //Then
        assertEquals(2, searchedProducts.size());
        assertEquals(name, searchedProducts.get(0).getName());
        assertEquals(name, searchedProducts.get(1).getName());

        verify(productRepository, times(1)).findByNameIgnoreCaseContaining(name);
    }

    @Test
    void givenNotExistingProductName_whenGetSearchedProducts_thenReturnEmptyList() {
        //Given
        String name = "Amazing football boots";
        when(productRepository.findByNameIgnoreCaseContaining(name)).thenReturn(List.of());

        //When
        List<Product> searchedProducts = productService.getSearchedProducts(name);

        //Then
        assertTrue(searchedProducts.isEmpty());

        verify(productRepository, times(1)).findByNameIgnoreCaseContaining(name);
    }




















}
