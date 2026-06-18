package com.server.models;

import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Date;

import lombok.Getter;

@Getter
@Document(collection = "users")
public class UserModel {

    public String _id;
    public String email;
    public String password;
    public String username;
    public String usertype;
    public Number balance;
    
    // Personal Information
    public String firstName;
    public String lastName;
    public String phone;
    public String address;
    public String city;
    public String state;
    public String zipCode;
    
    // Trading Preferences
    public String defaultOrderType;
    public String riskTolerance;
    public Number maxOrderAmount;
    
    // Display Preferences
    public String theme;
    public String currency;
    public String timezone;
    
    // Notification Preferences
    public List<String> emailNotifications;
    public List<String> pushNotifications;
    
    // Timestamps
    public Date createdAt;
    public Date updatedAt;

    public UserModel() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public UserModel(String _id, String email, String password, String username, String usertype, Number balance) {
        this._id = _id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.usertype = usertype;
        this.balance = balance;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters
    public String get_id() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getUsertype() {
        return usertype;
    }

    public Number getBalance() {
        return balance;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getDefaultOrderType() {
        return defaultOrderType;
    }

    public String getRiskTolerance() {
        return riskTolerance;
    }

    public Number getMaxOrderAmount() {
        return maxOrderAmount;
    }

    public String getTheme() {
        return theme;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTimezone() {
        return timezone;
    }

    public List<String> getEmailNotifications() {
        return emailNotifications;
    }

    public List<String> getPushNotifications() {
        return pushNotifications;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void set_id(String _id) {
        this._id = _id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public void setBalance(Number balance) {
        this.balance = balance;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setDefaultOrderType(String defaultOrderType) {
        this.defaultOrderType = defaultOrderType;
    }

    public void setRiskTolerance(String riskTolerance) {
        this.riskTolerance = riskTolerance;
    }

    public void setMaxOrderAmount(Number maxOrderAmount) {
        this.maxOrderAmount = maxOrderAmount;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setEmailNotifications(List<String> emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public void setPushNotifications(List<String> pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserModel [_id=" + _id + ", email=" + email + ", password=" + password
                + ", username=" + username + ", usertype=" + usertype + ", balance=" + balance + "]";
    }
}