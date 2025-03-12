package com.footballstore.web.dto;

import com.footballstore.product.model.Brand;
import com.footballstore.product.model.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Data
@Builder

@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Name is required.")
    private String name;

    @Size(min = 10, max = 1000, message = "Description length must be between 10 and 1000 characters!")
    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Image is required")
    @URL
    private String imageUrl;

    @NotNull(message = "Brand is required")
    private Brand brand;

    @NotNull
    private boolean isInStock;
}
