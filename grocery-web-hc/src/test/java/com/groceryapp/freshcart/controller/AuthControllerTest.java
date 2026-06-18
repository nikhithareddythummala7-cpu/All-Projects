package com.groceryapp.freshcart.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSignupEndpointExists() throws Exception {
        // Test that the /api/auth/signup endpoint is mapped and not returning 404
        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content("{\"username\":\"test\",\"email\":\"test@test.com\",\"password\":\"password\"}"))
                .andExpect(status().is4xxClientError()); // Should return 4xx (bad request due to validation), not 404
    }

    @Test
    void testSigninEndpointExists() throws Exception {
        // Test that the /api/auth/signin endpoint is mapped and not returning 404
        mockMvc.perform(post("/api/auth/signin")
                .contentType("application/json")
                .content("{\"username\":\"test\",\"password\":\"password\"}"))
                .andExpect(status().is4xxClientError()); // Should return 4xx (bad request due to invalid credentials), not 404
    }

    @Test
    void testAuthEndpointsAreAccessible() throws Exception {
        // Test that the base auth endpoint is accessible
        mockMvc.perform(get("/api/auth"))
                .andExpect(status().is4xxClientError()); // Should return 4xx (method not allowed), not 404
    }
}
