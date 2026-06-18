package com.server.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.server.models.OrderModel;
import com.server.models.StockModel;
import com.server.models.TransactionModel;
import com.server.models.UserModel;
import com.server.repos.OrderRepo;
import com.server.repos.StockRepo;
import com.server.repos.TransactionRepo;
import com.server.repos.UserRepo;


@CrossOrigin(origins="http://localhost:3000")
@RestController
@Controller
public class RouteController {
    
    @Autowired
    UserRepo userRepo;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    StockRepo stockRepo;

    @Autowired
    TransactionRepo transactionRepo;

    @PostMapping("/register")
    public UserModel registerMethod(@RequestBody UserModel userData) {
        try {

            userData.setBalance(0);
            UserModel user = userRepo.save(userData);
            return user;

        } catch (Exception e) {

            return null;
        }
    }

    @PostMapping("/login")
    public UserModel loginMethod(@RequestBody UserModel userData) {
        try {
            UserModel user = userRepo.findByEmail(userData.getEmail());

            if (user.getPassword().equals(userData.getPassword())) {

                return user;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/deposit")
    public TransactionModel depositMethod(@RequestBody TransactionModel transaction) {
        try {
            Optional<UserModel> userData = userRepo.findById(transaction.getUser());
            UserModel user = userData.get();
            user.setBalance(user.getBalance().doubleValue() + transaction.getAmount().doubleValue());

            userRepo.save(user);

            DateTimeFormatter currentDate = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            transaction.setTime(currentDate.format(now));
            transaction.setType("Deposit");

            transactionRepo.save(transaction);

            return transaction;
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/withdraw")
    public TransactionModel withdrawMethod(@RequestBody TransactionModel transaction) {
        try {
            Optional<UserModel> userData = userRepo.findById(transaction.getUser());
            UserModel user = userData.get();
            user.setBalance(user.getBalance().doubleValue() - transaction.getAmount().doubleValue());

            userRepo.save(user);

            DateTimeFormatter currentDate = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            transaction.setTime(currentDate.format(now));
            transaction.setType("Withdraw");

            transactionRepo.save(transaction);

            return transaction;
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/fetch-user/{id}")
    public UserModel fetchUserMethod(@PathVariable("id") String id) {
        try {
            Optional<UserModel> user = userRepo.findById(id);
            return user.get();
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/transactions")
    public List<TransactionModel> fetchTransactionsMethod() {
        try {
            return transactionRepo.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/buyStock")
    public OrderModel buyStockMethod(@RequestBody OrderModel order){

        Optional<UserModel> userData = userRepo.findById(order.getUser());

        StockModel stockData = stockRepo.findByUserAndSymbol(order.getUser(), order.getSymbol());

        if (stockData != null){

            StockModel stock = stockData;
            stockData.setCount(stock.getCount().doubleValue() + order.getCount().doubleValue());
            stockData.setTotalPrice(stock.getTotalPrice().doubleValue() + order.getTotalPrice().doubleValue());

            stockRepo.save(stockData);
        }else{

            StockModel stock = new StockModel();
            stock.setUser(order.getUser());
            stock.setSymbol(order.getSymbol());
            stock.setName(order.getName());
            stock.setPrice(order.getPrice());
            stock.setCount(order.getCount());
            stock.setTotalPrice(order.getTotalPrice());
            
            stockRepo.save(stock);
        }

        

        UserModel user = userData.get();

        user.setBalance(user.getBalance().doubleValue() - order.getTotalPrice().doubleValue());

        userRepo.save(user);

        order.setOrderType("buy");
        order.setOrderStatus("completed");

        return orderRepo.save(order);
    }

    @PostMapping("/sellStock")
    public OrderModel sellStockMethod(@RequestBody OrderModel order){

        Optional<UserModel> userData = userRepo.findById(order.getUser());

        StockModel stock = stockRepo.findByUserAndSymbol(order.getUser(), order.getSymbol());

        stock.setCount(stock.getCount().doubleValue() - order.getCount().doubleValue());
        stock.setTotalPrice(stock.getPrice().doubleValue() * stock.getCount().doubleValue());

        stockRepo.save(stock);

        UserModel user = userData.get();

        user.setBalance(user.getBalance().doubleValue() + order.getTotalPrice().doubleValue());

        userRepo.save(user);
        order.setOrderType("sell");
        order.setOrderStatus("completed");
        return orderRepo.save(order);
    }

    @GetMapping("/fetch-stocks")
    public List<StockModel> fetchStocksMethod() {
        try {
            return stockRepo.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/fetch-orders")
    public List<OrderModel> fetchOrdersMethod() {
        try {
            return orderRepo.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/fetch-users")
    public List<UserModel> fetchUsersMethod() {
        try {
            return userRepo.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    // Profile Management APIs
    @PutMapping("/update-profile/{id}")
    public UserModel updateProfileMethod(@PathVariable("id") String id, @RequestBody UserModel userData) {
        try {
            Optional<UserModel> userOptional = userRepo.findById(id);
            if (userOptional.isPresent()) {
                UserModel user = userOptional.get();
                
                // Update only the provided fields
                if (userData.getFirstName() != null) user.setFirstName(userData.getFirstName());
                if (userData.getLastName() != null) user.setLastName(userData.getLastName());
                if (userData.getEmail() != null) user.setEmail(userData.getEmail());
                if (userData.getPhone() != null) user.setPhone(userData.getPhone());
                if (userData.getAddress() != null) user.setAddress(userData.getAddress());
                if (userData.getCity() != null) user.setCity(userData.getCity());
                if (userData.getState() != null) user.setState(userData.getState());
                if (userData.getZipCode() != null) user.setZipCode(userData.getZipCode());
                
                return userRepo.save(user);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @PutMapping("/change-password/{id}")
    public UserModel changePasswordMethod(@PathVariable("id") String id, @RequestBody PasswordChangeRequest request) {
        try {
            Optional<UserModel> userOptional = userRepo.findById(id);
            if (userOptional.isPresent()) {
                UserModel user = userOptional.get();
                
                // Verify current password
                if (user.getPassword().equals(request.getCurrentPassword())) {
                    user.setPassword(request.getNewPassword());
                    return userRepo.save(user);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @PutMapping("/update-preferences/{id}")
    public UserModel updatePreferencesMethod(@PathVariable("id") String id, @RequestBody UserModel userData) {
        try {
            Optional<UserModel> userOptional = userRepo.findById(id);
            if (userOptional.isPresent()) {
                UserModel user = userOptional.get();
                
                // Update preferences
                if (userData.getDefaultOrderType() != null) user.setDefaultOrderType(userData.getDefaultOrderType());
                if (userData.getRiskTolerance() != null) user.setRiskTolerance(userData.getRiskTolerance());
                if (userData.getMaxOrderAmount() != null) user.setMaxOrderAmount(userData.getMaxOrderAmount());
                
                return userRepo.save(user);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @PutMapping("/update-display/{id}")
    public UserModel updateDisplayMethod(@PathVariable("id") String id, @RequestBody UserModel userData) {
        try {
            Optional<UserModel> userOptional = userRepo.findById(id);
            if (userOptional.isPresent()) {
                UserModel user = userOptional.get();
                
                // Update display settings
                if (userData.getTheme() != null) user.setTheme(userData.getTheme());
                if (userData.getCurrency() != null) user.setCurrency(userData.getCurrency());
                if (userData.getTimezone() != null) user.setTimezone(userData.getTimezone());
                
                return userRepo.save(user);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @PutMapping("/update-email-notifications/{id}")
    public UserModel updateEmailNotificationsMethod(@PathVariable("id") String id, @RequestBody NotificationRequest request) {
        try {
            Optional<UserModel> userOptional = userRepo.findById(id);
            if (userOptional.isPresent()) {
                UserModel user = userOptional.get();
                user.setEmailNotifications(request.getEmailNotifications());
                return userRepo.save(user);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @PutMapping("/update-push-notifications/{id}")
    public UserModel updatePushNotificationsMethod(@PathVariable("id") String id, @RequestBody NotificationRequest request) {
        try {
            Optional<UserModel> userOptional = userRepo.findById(id);
            if (userOptional.isPresent()) {
                UserModel user = userOptional.get();
                user.setPushNotifications(request.getPushNotifications());
                return userRepo.save(user);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // Helper classes for request bodies
    public static class PasswordChangeRequest {
        private String currentPassword;
        private String newPassword;
        
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class NotificationRequest {
        private java.util.List<String> emailNotifications;
        private java.util.List<String> pushNotifications;
        
        public java.util.List<String> getEmailNotifications() { return emailNotifications; }
        public void setEmailNotifications(java.util.List<String> emailNotifications) { this.emailNotifications = emailNotifications; }
        public java.util.List<String> getPushNotifications() { return pushNotifications; }
        public void setPushNotifications(java.util.List<String> pushNotifications) { this.pushNotifications = pushNotifications; }
    }

}
