package com.footballstore.web;

import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final UserService userService;

    @Autowired
    public CartController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getCart(){
        User user = userService.getUserById(UUID.fromString("487ef20b-bcc8-497b-a1fa-35f5ec610995"));

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("user", user);

        modelAndView.setViewName("cart");

        return modelAndView;
    }
}
