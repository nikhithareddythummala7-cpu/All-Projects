package com.example.table_booking.controller;

import com.example.table_booking.model.User;
import com.example.table_booking.model.User.UserRole;
import com.example.table_booking.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private String testUserToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole(UserRole.CUSTOMER);
        testUser = userRepository.save(testUser);

        // Get authentication token
        String requestBody = "{\"username\":\"test@example.com\",\"password\":\"password123\"}";
        
        ResultActions result = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
        
        String response = result.andReturn().getResponse().getContentAsString();
        // Parse JSON response to extract token safely
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        java.util.Map<String, String> map = mapper.readValue(response, new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String,String>>(){});
        testUserToken = map.get("jwt");
    }

    @AfterEach
    void tearDown() {
        // Clean up test data
        userRepository.deleteAll();
    }

    @Test
    void testGetCurrentUserRole_Authenticated() throws Exception {
        mockMvc.perform(get("/api/user/role")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("CUSTOMER")));
    }

    @Test
    void testGetCurrentUserRole_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/user/role")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        String newUserJson = """
            {
                "username": "newuser",
                "email": "newuser@example.com",
                "password": "newPassword123",
                "role": "CUSTOMER"
            }""";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.email", is("newuser@example.com")))
                .andExpect(jsonPath("$.role", is("CUSTOMER")));
    }

    @Test
    void testGetUserProfile_Authenticated() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    void testUpdateUserProfile() throws Exception {
        String updateJson = """
            {
                "username": "updateduser",
                "email": "updated@example.com"
            }""";

        mockMvc.perform(put("/api/user/profile")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updateduser")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));
    }
}
