package com.footballstore.web;

import com.footballstore.cart.service.CartService;
import com.footballstore.product.model.Product;
import com.footballstore.product.service.ProductService;
import com.footballstore.security.AuthenticationMetadata;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import com.footballstore.web.dto.ProductRequest;
import com.footballstore.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;

    @Autowired
    public ProductController(ProductService productService, UserService userService, CartService cartService) {
        this.productService = productService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping
    public ModelAndView getProductsPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getUserById(authenticationMetadata.getUserId());
        modelAndView.addObject("user", user);

        List<Product> products = productService.getAllProducts();

        modelAndView.addObject("products", products);


        modelAndView.setViewName("products");

        return modelAndView;

    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getProductsAdminPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getUserById(authenticationMetadata.getUserId());
        modelAndView.addObject("user", user);

        List<Product> products = productService.getAllProducts();

        modelAndView.addObject("products", products);

        modelAndView.setViewName("products-admin");

        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView getCreateProductPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getUserById(authenticationMetadata.getUserId());
        modelAndView.addObject("user", user);

        modelAndView.addObject("productRequest", ProductRequest.builder().build());

        modelAndView.setViewName("product-new");

        return modelAndView;
    }

    @PostMapping
    public ModelAndView createProduct(@Valid ProductRequest productRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView();

            User user = userService.getUserById(authenticationMetadata.getUserId());
            modelAndView.addObject("user", user);

            modelAndView.setViewName("product-new");

            return modelAndView;
        }

        productService.createProduct(productRequest);

        return new ModelAndView("redirect:/products/admin");

    }

    @GetMapping("/{id}")
    public ModelAndView getProductPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getUserById(authenticationMetadata.getUserId());
        modelAndView.addObject("user", user);

        Product product = productService.getProductById(id);
        modelAndView.addObject("product", product);

        modelAndView.setViewName("product");

        return modelAndView;

    }

    @GetMapping("/{id}/info")
    public ModelAndView getUpdateProductPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView modelAndView = new ModelAndView();

        Product product = productService.getProductById(id);

        User user = userService.getUserById(authenticationMetadata.getUserId());
        modelAndView.addObject("user", user);

        modelAndView.addObject("product", product);
        modelAndView.addObject("productRequest", DtoMapper.fromProduct(product));

        modelAndView.setViewName("product-update");

        return modelAndView;

    }

    @PutMapping("{id}/info")
    public ModelAndView updateProduct(@PathVariable UUID id, @Valid ProductRequest productRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView();

            Product product = productService.getProductById(id);

            User user = userService.getUserById(authenticationMetadata.getUserId());
            modelAndView.addObject("user", user);

            modelAndView.addObject("product", product);
            modelAndView.addObject("productRequest", productRequest);

            modelAndView.setViewName("product-update");

            return modelAndView;
        }

        productService.updateProduct(id, productRequest);

        return new ModelAndView("redirect:/products/admin");
    }

    @GetMapping("/browse")
    public ModelAndView getProductsByCategory(@RequestParam(name = "category") String category, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getUserById(authenticationMetadata.getUserId());
        modelAndView.addObject("user", user);

        List<Product> productsByCategory = productService.getProductsByCategory(category);

        modelAndView.addObject("productsByCategory", productsByCategory);
        modelAndView.addObject("category", category);

        modelAndView.setViewName("browse-category");

        return modelAndView;
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable UUID id){
        cartService.deleteCartItem(id);
        productService.deleteProduct(id);

        return "redirect:/products/admin";

    }

}
