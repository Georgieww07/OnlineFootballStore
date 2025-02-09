package com.footballstore.web.dto;

import com.footballstore.product.model.Brand;
import com.footballstore.product.model.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductRequest {
    @NotBlank(message = "Name is required.")
    private String name;

    @NotNull(message = "Description is required.")
    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Image is required")
    private String imageUrl;

    @NotNull(message = "Brand is required")
    private Brand brand;

    @NotNull
    private boolean isInStock;
}
