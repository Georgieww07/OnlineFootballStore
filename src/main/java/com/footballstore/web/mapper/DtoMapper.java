package com.footballstore.web.mapper;

import com.footballstore.product.model.Product;
import com.footballstore.user.model.User;
import com.footballstore.web.dto.ProductRequest;
import com.footballstore.web.dto.UserEditRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static UserEditRequest fromUser(User user) {

        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public static ProductRequest fromProduct(Product product) {
        return ProductRequest.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .brand(product.getBrand())
                .isInStock(product.isInStock())
                .build();
    }
}
