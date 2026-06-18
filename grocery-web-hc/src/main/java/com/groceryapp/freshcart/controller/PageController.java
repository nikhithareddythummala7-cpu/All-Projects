package com.groceryapp.freshcart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/signup")
	public String signup() {
		return "signup";
	}

	@GetMapping("/products")
	public String products() {
		return "products";
	}

	@GetMapping("/cart")
	public String cart() {
		return "cart";
	}

	@GetMapping("/cart/{any}")
	public String cartDeepLink() {
		return "cart";
	}

	@GetMapping("/orders")
	public String orders() {
		return "orders";
	}

	@GetMapping("/orders/{any}")
	public String ordersDeepLink() {
		return "orders";
	}

	@GetMapping("/checkout")
	public String checkout() {
		return "checkout";
	}

	@GetMapping("/admin")
	public String admin() {
		return "admin";
	}

	@GetMapping("/admin/products")
	public String adminProducts() {
		return "admin-products";
	}
}
