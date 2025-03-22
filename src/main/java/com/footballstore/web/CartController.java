package com.footballstore.web;

import com.footballstore.cart.model.Cart;
import com.footballstore.cart.service.CartService;
import com.footballstore.cart.model.CartItem;
import com.footballstore.security.AuthenticationMetadata;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final UserService userService;
    private final CartService cartService;

    @Autowired
    public CartController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping
    public ModelAndView getCart(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getUserById(authenticationMetadata.getUserId());
        Cart cart = cartService.getCartByUserId(user.getId());
        List<CartItem> cartItems = cart.getItems();

        BigDecimal cartTotal = cartService.getCartTotal(cart);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("cartTotal", cartTotal);
        modelAndView.addObject("cartItems", cartItems);
        modelAndView.addObject("user", user);
        modelAndView.setViewName("cart");

        return modelAndView;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") UUID productId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());

        cartService.addToCart(user.getId(), productId);

        return "redirect:/products";
    }

    @DeleteMapping("/{id}")
    public String removeCartItem(@PathVariable UUID id) {
        cartService.deleteCartItem(id);

        return "redirect:/cart";

    }

}
