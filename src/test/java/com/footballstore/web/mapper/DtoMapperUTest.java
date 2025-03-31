package com.footballstore.web.mapper;

import com.footballstore.product.model.Brand;
import com.footballstore.product.model.Category;
import com.footballstore.product.model.Product;
import com.footballstore.user.model.User;
import com.footballstore.web.dto.ProductRequest;
import com.footballstore.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

    @Test
    void givenHappyPath_whenMappingProductToProductRequest(){
        //Given
        Product product = Product.builder()
                .name("Product")
                .description("Amazing Product")
                .price(BigDecimal.TEN)
                .category(Category.BOOTS)
                .imageUrl("http://example.com/football.jpg")
                .brand(Brand.NIKE)
                .isInStock(true)
                .build();

        //When
        ProductRequest productRequest = DtoMapper.fromProduct(product);

        //Then
        assertEquals(product.getName(), productRequest.getName());
        assertEquals(product.getDescription(), productRequest.getDescription());
        assertEquals(product.getPrice(), productRequest.getPrice());
        assertEquals(product.getCategory(), productRequest.getCategory());
        assertEquals(product.getImageUrl(), productRequest.getImageUrl());
        assertEquals(product.getBrand(), productRequest.getBrand());
        assertTrue(productRequest.isInStock());
    }

    @Test
    void givenHappyPath_whenMappingUserToUserEditRequest(){
        //Given
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+359895432786")
                .build();

        //When
        UserEditRequest userEditRequest = DtoMapper.fromUser(user);

        //Then
        assertEquals(user.getFirstName(), userEditRequest.getFirstName());
        assertEquals(user.getLastName(), userEditRequest.getLastName());
        assertEquals(user.getPhoneNumber().substring(4, 13), userEditRequest.getPhoneNumber());
    }

    @Test
    void givenNullPhoneNumber_whenMappingUserToUserEditRequest(){
        //Given
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber(null)
                .build();

        //When
        UserEditRequest userEditRequest = DtoMapper.fromUser(user);

        //Then
        assertEquals(user.getFirstName(), userEditRequest.getFirstName());
        assertEquals(user.getLastName(), userEditRequest.getLastName());
        assertEquals("", userEditRequest.getPhoneNumber());
    }
}