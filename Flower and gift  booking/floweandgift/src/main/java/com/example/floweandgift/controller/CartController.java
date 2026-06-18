package com.example.floweandgift.controller;

import com.example.floweandgift.model.Cart;
import com.example.floweandgift.model.Product;
import com.example.floweandgift.model.User;
import com.example.floweandgift.service.CartService;
import com.example.floweandgift.service.ProductService;
import com.example.floweandgift.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String userId = getUserId(userDetails);
        Cart cart = cartService.getCartByUserId(userId);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam String productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {
        String userId = getUserId(userDetails);
        cartService.addItemToCart(userId, productId, quantity);
        redirectAttributes.addFlashAttribute("message", "Item added to cart");
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCartItem(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String productId,
                                 @RequestParam int quantity,
                                 RedirectAttributes redirectAttributes) {
        String userId = getUserId(userDetails);
        if (quantity > 0) {
            cartService.updateItemQuantity(userId, productId, quantity);
            redirectAttributes.addFlashAttribute("message", "Cart updated");
        } else {
            cartService.removeItemFromCart(userId, productId);
            redirectAttributes.addFlashAttribute("message", "Item removed from cart");
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String productId,
                                 RedirectAttributes redirectAttributes) {
        String userId = getUserId(userDetails);
        cartService.removeItemFromCart(userId, productId);
        redirectAttributes.addFlashAttribute("message", "Item removed from cart");
        return "redirect:/cart";
    }

  

private String getUserId(UserDetails userDetails) {
    if (userDetails == null) {
        return null;
    }
    return userService.findByUsername(userDetails.getUsername())
                      .map(User::getId)
                      .orElse(null);
}

}
