package com.example.floweandgift.controller;

import com.example.floweandgift.model.Address;
import com.example.floweandgift.model.Cart;
import com.example.floweandgift.model.Order;
import com.example.floweandgift.model.Payment;
import com.example.floweandgift.model.User;
import com.example.floweandgift.service.CartService;
import com.example.floweandgift.service.CouponService;
import com.example.floweandgift.service.OrderService;
import com.example.floweandgift.service.PaymentService;
import com.example.floweandgift.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CouponService couponService;

    // ============================
    // BUY NOW - Add single product and proceed to checkout
    // ============================

    @GetMapping("/buy-now")
    public String buyNow(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestParam String productId,
                         @RequestParam(defaultValue = "1") int quantity,
                         RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        String userId = getUserId(userDetails);

        try {
            // Add product to cart
            cartService.addItemToCart(userId, productId, quantity);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not add product to cart: " + e.getMessage());
            return "redirect:/products";
        }

        // Redirect to checkout
        return "redirect:/checkout";
    }

    // ============================
    // GET CHECKOUT PAGE
    // ============================

    @GetMapping
    public String checkout(@AuthenticationPrincipal UserDetails userDetails,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        String userId = getUserId(userDetails);
        Cart cart = cartService.getCartByUserId(userId);
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);

        // If cart is empty → back to cart
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        // If no addresses → force user to profile to add one
        if (user == null || user.getAddresses() == null || user.getAddresses().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please add at least one delivery address in your profile before checkout."
            );
            return "redirect:/user/profile";
        }

        List<Address> addressList = user.getAddresses();

        model.addAttribute("cart", cart);
        model.addAttribute("addresses", addressList);
        model.addAttribute("order", new Order());

        return "checkout";
    }

    // ============================
    // PLACE ORDER
    // ============================

    @PostMapping
    public String placeOrder(@AuthenticationPrincipal UserDetails userDetails,
                             @ModelAttribute Order order,
                             @RequestParam String addressIndex,
                             @RequestParam String paymentMethod,
                             @RequestParam String deliveryDayOption,
                             @RequestParam String deliverySlot,
                             @RequestParam(required = false) String couponCode,
                             RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        String userId = getUserId(userDetails);
        Cart cart = cartService.getCartByUserId(userId);
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        // -------------------------------
        // Copy cart items to order
        // -------------------------------
        order.setUserId(userId);
        order.setItems(cart.getItems().stream()
                .map(item -> new Order.OrderItem(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice()))
                .collect(Collectors.toList()));

        // -------------------------------
        // Apply coupon
        // -------------------------------
        double cartTotal = cart.getTotalAmount();
        double discount = 0.0;

        if (couponCode != null && !couponCode.isBlank()) {
            if (couponService.validateCoupon(couponCode, cartTotal)) {
                discount = couponService.applyCoupon(couponCode, cartTotal);
                order.setCouponCode(couponCode);
                order.setDiscountAmount(discount);
            } else {
                redirectAttributes.addFlashAttribute("couponError", "Invalid or expired coupon");
                return "redirect:/checkout";
            }
        }

        // -------------------------------
        // Delivery slot fee
        // -------------------------------
        double deliveryFee = 0.0;

        // One slot free, others ₹99
        if ("07:00-21:00".equals(deliverySlot)) {
            deliveryFee = 0.0;
        } else if ("08:00-15:00".equals(deliverySlot) || "15:00-21:00".equals(deliverySlot)) {
            deliveryFee = 99.0;
        }

        // Save delivery info on order (make sure these fields exist in Order)
        order.setDeliveryDayOption(deliveryDayOption);
        order.setDeliverySlot(deliverySlot);
        order.setDeliveryFee(deliveryFee);

        // Final total = cart - discount + delivery fee
        order.setTotalAmount(cartTotal - discount + deliveryFee);

        // -------------------------------
        // Set delivery date time to avoid NPE in order details view
        // Recommendation: compute this properly based on deliveryDayOption and deliverySlot
        // For now, set to current datetime to fix error
        // -------------------------------
        order.setDeliveryDateTime(java.time.LocalDateTime.now());

        // -------------------------------
        // Set delivery address safely
        // -------------------------------
        if (user != null && user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            int index = -1;
            try {
                if (addressIndex != null && !addressIndex.isBlank()) {
                    index = Integer.parseInt(addressIndex);
                } else {
                    redirectAttributes.addFlashAttribute("error", "No address selected");
                    return "redirect:/checkout";
                }
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("error", "Invalid address format");
                return "redirect:/checkout";
            }

            if (index >= 0 && index < user.getAddresses().size()) {
                order.setDeliveryAddress(user.getAddresses().get(index));
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid address selected");
                return "redirect:/checkout";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "No address found, please add one.");
            return "redirect:/user/profile";
        }

        // -------------------------------
        // Payment processing with safe enum parsing
        // -------------------------------
        Payment payment = new Payment();
        try {
            payment.setMethod(Payment.PaymentMethod.valueOf(paymentMethod.toUpperCase().replace(" ", "_")));
        } catch (IllegalArgumentException | NullPointerException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid payment method selected");
            return "redirect:/checkout";
        }

        if (payment.getMethod() == Payment.PaymentMethod.ONLINE) {
            payment = paymentService.processPayment(payment);
            if (payment == null) {
                redirectAttributes.addFlashAttribute("error", "Payment processing failed");
                return "redirect:/checkout";
            }
        } else {
            payment.setStatus(Payment.PaymentStatus.PENDING);
        }

        order.setPayment(payment);

        // -------------------------------
        // Save order
        // -------------------------------
        Order savedOrder = orderService.placeOrder(order);

        // -------------------------------
        // Clear cart
        // -------------------------------
        cartService.clearCart(userId);

        // Redirect to order details
        return "redirect:/orders/" + savedOrder.getId();
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
