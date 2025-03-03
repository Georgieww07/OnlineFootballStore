package com.footballstore.web;

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

import java.util.UUID;

@Controller
public class IndexController {
    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
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
        User user = userService.getUserById(UUID.fromString("487ef20b-bcc8-497b-a1fa-35f5ec610995"));

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("user", user);

        modelAndView.setViewName("home");

        return modelAndView;
    }

}
