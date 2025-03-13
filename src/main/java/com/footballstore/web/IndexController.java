package com.footballstore.web;

import com.footballstore.product.model.Product;
import com.footballstore.product.service.ProductService;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import com.footballstore.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class IndexController {
    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public IndexController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping("/")
    public String getIndexPage(){
        return "index";
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage(){
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("registerRequest", RegisterRequest.builder().build());

        modelAndView.setViewName("register");

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new ModelAndView("register");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("register");
            modelAndView.addObject("errorMessage", "Passwords do not match!");
            return modelAndView;
        }
        userService.registerUser(registerRequest);

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/login")
    public String getLoginPage(){
        System.out.println();
        return "login";
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(){
        //TODO:Remove these two lines they are for testing
        User user = userService.getUserById(UUID.fromString("0fe1122a-fa46-4962-8a15-f666c3de8eed"));

        List<Product> featuredProducts = productService.getFeaturedProducts();

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("user", user);
        modelAndView.addObject("featuredProducts", featuredProducts);

        modelAndView.setViewName("home");

        return modelAndView;
    }
}
