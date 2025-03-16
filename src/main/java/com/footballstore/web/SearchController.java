package com.footballstore.web;

import com.footballstore.product.model.Product;
import com.footballstore.product.service.ProductService;
import com.footballstore.security.AuthenticationMetadata;
import com.footballstore.user.model.User;
import com.footballstore.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public SearchController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView search(@RequestParam(name = "query") String query, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        List<Product> searchResults = productService.getSearchedProducts(query);
        User user = userService.getUserById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("searchResults", searchResults);
        modelAndView.addObject("user", user);
        modelAndView.addObject("query", query);
        modelAndView.setViewName("search-results");

        return modelAndView;

    }
}
