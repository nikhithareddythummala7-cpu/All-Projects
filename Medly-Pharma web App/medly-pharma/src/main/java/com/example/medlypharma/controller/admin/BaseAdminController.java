package com.example.medlypharma.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseAdminController {
    
    @ModelAttribute
    public void addCommonAttributes(Model model, HttpServletRequest request) {
        // Add the current URI for highlighting active menu items
        String requestUri = request.getRequestURI();
        model.addAttribute("currentUri", requestUri);
        
        // Add common attributes needed by the admin layout
        model.addAttribute("appName", "Medly Pharma");
        model.addAttribute("adminName", "Admin User"); // You can customize this
    }
    
    protected String adminView(String viewName) {
        return "admin/" + viewName;
    }
}
