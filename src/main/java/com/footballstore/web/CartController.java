package com.footballstore.web;

import com.footballstore.cart.model.Cart;
import com.footballstore.cart.service.CartService;
import com.footballstore.cartitem.model.CartItem;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ModelAndView getCart(){
        User user = userService.getUserById(UUID.fromString("0fe1122a-fa46-4962-8a15-f666c3de8eed"));

        ModelAndView modelAndView = new ModelAndView();

        List<CartItem> cartItems = cartService.getCartItems();

        Cart cart = user.getCart();
        if (user.getCart() == null){
            cart = cartService.initCart(user);
        }

        BigDecimal cartTotal = cartService.getCartTotal(cart);
        modelAndView.addObject("cartTotal", cartTotal);

        modelAndView.addObject("cartItems", cartItems);

        modelAndView.addObject("user", user);

        modelAndView.setViewName("cart");

        return modelAndView;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") UUID productId) {


        //TODO: remove these two lines they are for testing
        User user = userService.getUserById(UUID.fromString("0fe1122a-fa46-4962-8a15-f666c3de8eed"));

        cartService.addToCart(user.getId(), productId);

        return "redirect:/products";
    }

    @DeleteMapping("/{id}")
    public String removeCartItem(@PathVariable UUID id) {
        cartService.deleteCartItem(id);

        return "redirect:/cart";

    }

}
