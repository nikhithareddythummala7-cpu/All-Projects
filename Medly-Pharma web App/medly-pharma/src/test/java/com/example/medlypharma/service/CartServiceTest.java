package com.example.medlypharma.service;

import com.example.medlypharma.model.CartItem;
import com.example.medlypharma.model.User;
import com.example.medlypharma.repository.CartRepository;
import com.example.medlypharma.dto.CartResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CartServiceTest {

    private CartRepository cartRepository;
    private MedicineService medicineService;
    private UserService userService;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartRepository = Mockito.mock(CartRepository.class);
        medicineService = Mockito.mock(MedicineService.class);
        userService = Mockito.mock(UserService.class);
        cartService = new CartService(cartRepository, medicineService, userService);
    }

    @Test
    void getCart_emptyCart_shouldReturnZeroTotals() {
        User u = new User(); u.setId("u1");
        when(userService.getUserByEmail(anyString())).thenReturn(u);
        when(cartRepository.findByUserId("u1")).thenReturn(Collections.emptyList());

        CartResponseDTO resp = cartService.getCart("test@example.com");
        assertNotNull(resp);
        assertEquals(0, resp.getTotalItems());
        assertEquals(0.0, resp.getTotalAmount());
        assertEquals(0.0, resp.getDeliveryCost());
        assertTrue(resp.getItems().isEmpty());
    }

    @Test
    void getCart_tieredDelivery_freeOver1000() {
        User u = new User(); u.setId("u1");
        when(userService.getUserByEmail(anyString())).thenReturn(u);

        CartItem a = new CartItem();
        a.setId("c1"); a.setMedicineId("m1"); a.setMedicineName("A"); a.setQuantity(10); a.setPrice(120.0); // 1200

        when(cartRepository.findByUserId("u1")).thenReturn(List.of(a));

        CartResponseDTO resp = cartService.getCart("test@example.com");
        assertEquals(1, resp.getItems().size());
        assertEquals(1200.0, resp.getTotalAmount(), 0.001);
        assertEquals(0.0, resp.getDeliveryCost(), 0.001);
        assertEquals(10, resp.getTotalItems());
    }

    @Test
    void getCart_tieredDelivery_25Between500And999() {
        User u = new User(); u.setId("u1");
        when(userService.getUserByEmail(anyString())).thenReturn(u);

        CartItem a = new CartItem();
        a.setId("c1"); a.setMedicineId("m1"); a.setMedicineName("A"); a.setQuantity(3); a.setPrice(200.0); // 600

        when(cartRepository.findByUserId("u1")).thenReturn(List.of(a));

        CartResponseDTO resp = cartService.getCart("test@example.com");
        assertEquals(600.0, resp.getTotalAmount(), 0.001);
        assertEquals(25.0, resp.getDeliveryCost(), 0.001);
        assertEquals(3, resp.getTotalItems());
    }

    @Test
    void getCart_tieredDelivery_50Below500() {
        User u = new User(); u.setId("u1");
        when(userService.getUserByEmail(anyString())).thenReturn(u);

        CartItem a = new CartItem();
        a.setId("c1"); a.setMedicineId("m1"); a.setMedicineName("A"); a.setQuantity(2); a.setPrice(20.0); // 40

        when(cartRepository.findByUserId("u1")).thenReturn(List.of(a));

        CartResponseDTO resp = cartService.getCart("test@example.com");
        assertEquals(40.0, resp.getTotalAmount(), 0.001);
        assertEquals(50.0, resp.getDeliveryCost(), 0.001);
        assertEquals(2, resp.getTotalItems());
    }
}
