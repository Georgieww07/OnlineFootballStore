package com.footballstore.web;

import com.footballstore.security.AuthenticationMetadata;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import com.footballstore.web.dto.UserEditRequest;
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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getUserById(authenticationMetadata.getUserId());
        modelAndView.addObject("user", user);

        List<User> users = userService.getAllUsers();

        modelAndView.addObject("users", users);

        modelAndView.setViewName("users");

        return modelAndView;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView getUserProfilePage(@PathVariable UUID id){
        User user = userService.getUserById(id);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("user", user);
        modelAndView.addObject("userEditRequest", DtoMapper.fromUser(user));

        modelAndView.setViewName("profile");

        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView updateUserProfile(@PathVariable UUID id, @Valid UserEditRequest userEditRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            User user = userService.getUserById(id);

            ModelAndView modelAndView = new ModelAndView();

            modelAndView.addObject("user", user);
            modelAndView.addObject("userEditRequest", userEditRequest);

            modelAndView.setViewName("profile");

            return modelAndView;
        }

        userService.editUserInfo(id, userEditRequest);

        return new ModelAndView("redirect:/home");
    }

    @PutMapping("/{id}/role")
    public String changeRole(@PathVariable UUID id){

        userService.changeRole(id);

        return "redirect:/users";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable UUID id){

        userService.deleteUser(id);

        return "redirect:/users";
    }
}
