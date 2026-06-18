package com.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.server.models.OrderModel;
import com.server.models.StockModel;
import com.server.models.TransactionModel;
import com.server.models.UserModel;
import com.server.repos.OrderRepo;
import com.server.repos.StockRepo;
import com.server.repos.TransactionRepo;
import com.server.repos.UserRepo;

@Controller
public class ThymeleafController {
    
    @Autowired
    UserRepo userRepo;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    StockRepo stockRepo;

    @Autowired
    TransactionRepo transactionRepo;

    @GetMapping("/")
    public String home(Model model) {
        List<StockModel> stocks = stockRepo.findAll();
        model.addAttribute("stocks", stocks);
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        List<UserModel> users = userRepo.findAll();
        List<StockModel> stocks = stockRepo.findAll();
        List<OrderModel> orders = orderRepo.findAll();
        List<TransactionModel> transactions = transactionRepo.findAll();
        
        model.addAttribute("users", users);
        model.addAttribute("stocks", stocks);
        model.addAttribute("orders", orders);
        model.addAttribute("transactions", transactions);
        
        return "admin";
    }

    @GetMapping("/admin-stock")
    public String adminStock(Model model) {
        List<StockModel> stocks = stockRepo.findAll();
        model.addAttribute("stocks", stocks);
        return "admin-stock";
    }

    @GetMapping("/all-orders")
    public String allOrders(Model model) {
        List<OrderModel> orders = orderRepo.findAll();
        model.addAttribute("orders", orders);
        return "all-orders";
    }

    @GetMapping("/all-transactions")
    public String allTransactions(Model model) {
        List<TransactionModel> transactions = transactionRepo.findAll();
        model.addAttribute("transactions", transactions);
        return "all-transactions";
    }

    @GetMapping("/user/{id}")
    public String userProfile(@PathVariable String id, Model model) {
        try {
            UserModel user = userRepo.findById(id).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
                return "user-profile";
            }
        } catch (Exception e) {
            // Handle error
        }
        return "error";
    }

    @GetMapping("/portfolio")
    public String portfolio() {
        return "portfolio";
    }

    @GetMapping("/history")
    public String history() {
        return "history";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/stock/{symbol}")
    public String stockDetail(@PathVariable String symbol, Model model) {
        model.addAttribute("symbol", symbol);
        return "stock-detail";
    }
}
