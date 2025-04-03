package com.footballstore.web;

import com.footballstore.order.model.Order;
import com.footballstore.order.service.OrderService;
import com.footballstore.security.AuthenticationMetadata;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping
    public ModelAndView getOrdersPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        List<Order> orders = orderService.getOrdersByUser(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("orders", orders);
        modelAndView.setViewName("orders");

        return modelAndView;
    }

    @PostMapping
    public String placeOrder(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        orderService.placeOrder(user);

        return "redirect:/orders";
    }
}
