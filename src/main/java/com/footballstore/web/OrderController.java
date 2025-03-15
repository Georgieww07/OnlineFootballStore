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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

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

        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getUserById(authenticationMetadata.getUserId());
        modelAndView.addObject("user", user);

        List<Order> orders = orderService.getOrdersByUser(user);

        modelAndView.addObject("orders", orders);

        modelAndView.setViewName("orders");

        return modelAndView;
    }

    @PostMapping
    public ModelAndView placeOrder(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getUserById(authenticationMetadata.getUserId());

        modelAndView.addObject("user", user);

        orderService.placeOrder(user);

        return new ModelAndView("redirect:/orders");

    }
}
