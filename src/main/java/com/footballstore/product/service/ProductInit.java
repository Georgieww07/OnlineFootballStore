package com.footballstore.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductInit implements ApplicationRunner {
    private final ProductService productService;

    @Autowired
    public ProductInit(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(ApplicationArguments args) {

        productService.createAppProducts();
    }
}
