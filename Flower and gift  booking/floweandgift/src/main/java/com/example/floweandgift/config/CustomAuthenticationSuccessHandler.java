package com.example.floweandgift.config;

import com.example.floweandgift.model.User;
import com.example.floweandgift.service.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        User user = userDetailsService.findUserByUsername(username);

        if (user != null) {
            if (user.getRole() == User.Role.ADMIN) {
                response.sendRedirect("/admin/dashboard");
            } else if (user.getRole() == User.Role.CUSTOMER || user.getRole() == User.Role.USER) {
                response.sendRedirect("/");
            } else {
                response.sendRedirect("/");
            }
        } else {
            response.sendRedirect("/");
        }
    }
}
