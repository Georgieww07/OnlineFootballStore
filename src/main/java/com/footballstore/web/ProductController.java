package com.footballstore.web;

import com.footballstore.product.model.Product;
import com.footballstore.product.service.ProductService;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import com.footballstore.web.dto.ProductRequest;
import com.footballstore.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getProductsPage(){
        ModelAndView modelAndView = new ModelAndView();

        //TODO: remove these two lines they are for testing
        User user = userService.getUserById(UUID.fromString("0fe1122a-fa46-4962-8a15-f666c3de8eed"));
        modelAndView.addObject("user", user);

        List<Product> products = productService.getAllProducts();

        modelAndView.addObject("products", products);


        modelAndView.setViewName("products");

        return modelAndView;

    }

    @GetMapping("/admin")
    public ModelAndView getProductsAdminPage(){
        ModelAndView modelAndView = new ModelAndView();

        //TODO: remove these two lines they are for testing
        User user = userService.getUserById(UUID.fromString("0fe1122a-fa46-4962-8a15-f666c3de8eed"));
        modelAndView.addObject("user", user);

        List<Product> products = productService.getAllProducts();

        modelAndView.addObject("products", products);

        modelAndView.setViewName("products-admin");

        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView getCreateProductPage(){
        ModelAndView modelAndView = new ModelAndView();

        //TODO: remove these two lines they are for testing
        User user = userService.getUserById(UUID.fromString("0fe1122a-fa46-4962-8a15-f666c3de8eed"));
        modelAndView.addObject("user", user);

        modelAndView.addObject("productRequest", ProductRequest.builder().build());

        modelAndView.setViewName("product-new");

        return modelAndView;
    }

    @PostMapping
    public ModelAndView createProduct(@Valid ProductRequest productRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView();

            //TODO: remove these two lines they are for testing
            User user = userService.getUserById(UUID.fromString("0fe1122a-fa46-4962-8a15-f666c3de8eed"));
            modelAndView.addObject("user", user);

            modelAndView.setViewName("product-new");

            return modelAndView;
        }

        productService.createProduct(productRequest);

        return new ModelAndView("redirect:/products/admin");

    }

    @GetMapping("/{id}")
    public ModelAndView getProductPage(@PathVariable UUID id){
        ModelAndView modelAndView = new ModelAndView();

        //TODO: remove these two lines they are for testing
        User user = userService.getUserById(UUID.fromString("0fe1122a-fa46-4962-8a15-f666c3de8eed"));
        modelAndView.addObject("user", user);

        Product product = productService.getProductById(id);
        modelAndView.addObject("product", product);

        modelAndView.setViewName("product");

        return modelAndView;

    }

    @GetMapping("/{id}/info")
    public ModelAndView getUpdateProductPage(@PathVariable UUID id){

        ModelAndView modelAndView = new ModelAndView();

        Product product = productService.getProductById(id);

        //TODO: remove these two lines they are for testing
        User user = userService.getUserById(UUID.fromString("0fe1122a-fa46-4962-8a15-f666c3de8eed"));
        modelAndView.addObject("user", user);


        modelAndView.addObject("product", product);
        modelAndView.addObject("productRequest", DtoMapper.fromProduct(product));

        modelAndView.setViewName("product-update");

        return modelAndView;

    }

    @PutMapping("{id}/info")
    public ModelAndView updateProduct(@PathVariable UUID id, @Valid ProductRequest productRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView();

            Product product = productService.getProductById(id);

            //TODO: remove these two lines they are for testing
            User user = userService.getUserById(UUID.fromString("0fe1122a-fa46-4962-8a15-f666c3de8eed"));
            modelAndView.addObject("user", user);


            modelAndView.addObject("product", product);
            modelAndView.addObject("productRequest", productRequest);

            modelAndView.setViewName("product-update");

            return modelAndView;
        }

        productService.updateProduct(id, productRequest);

        return new ModelAndView("redirect:/products/admin");
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable UUID id){
        productService.deleteProduct(id);

        return "redirect:/products/admin";

    }

}
