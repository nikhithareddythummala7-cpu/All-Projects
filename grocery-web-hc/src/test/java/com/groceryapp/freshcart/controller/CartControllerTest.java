package com.groceryapp.freshcart.controller;

import com.groceryapp.freshcart.model.CartItem;
import com.groceryapp.freshcart.model.Product;
import com.groceryapp.freshcart.model.User;
import com.groceryapp.freshcart.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private User testUser;
    private Product testProduct;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(10.99));
        testProduct.setStock(100);
        
        // Setup test cart item
        testCartItem = new CartItem(testUser, testProduct, 2);
        testCartItem.setId(1L);
        
        // Setup security context
        Authentication auth = new UsernamePasswordAuthenticationToken(
            testUser, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testGetCartItems_Success() {
        // Arrange
        List<CartItem> cartItems = Arrays.asList(testCartItem);
        when(cartService.getUserCart(1L)).thenReturn(cartItems);

        // Act
        ResponseEntity<?> response = cartController.getCartItems(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(cartItems, response.getBody());
        verify(cartService).getUserCart(1L);
    }

    @Test
    void testAddCartItem_Success() {
        // Arrange
        CartController.CartItemRequest request = new CartController.CartItemRequest();
        request.setUserId(1L);
        request.setProductId(1L);
        request.setQuantity(2);
        
        when(cartService.addToCart(1L, 1L, 2)).thenReturn(testCartItem);

        // Act
        ResponseEntity<?> response = cartController.addCartItem(request);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(testCartItem, response.getBody());
        verify(cartService).addToCart(1L, 1L, 2);
    }

    @Test
    void testUpdateCartItemQuantity_Success() {
        // Arrange
        when(cartService.updateCartItemQuantity(1L, 5)).thenReturn(testCartItem);

        // Act
        ResponseEntity<?> response = cartController.updateCartItemQuantity(1L, 5);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(testCartItem, response.getBody());
        verify(cartService).updateCartItemQuantity(1L, 5);
    }

    @Test
    void testUpdateCartItemQuantity_RemoveItem() {
        // Arrange - when quantity is 0 or negative, the service returns null
        when(cartService.updateCartItemQuantity(1L, 0)).thenReturn(null);

        // Act
        ResponseEntity<?> response = cartController.updateCartItemQuantity(1L, 0);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Item removed from cart successfully", response.getBody());
        verify(cartService).updateCartItemQuantity(1L, 0);
    }

    @Test
    void testRemoveCartItem_Success() {
        // Arrange
        doNothing().when(cartService).removeCartItem(1L);

        // Act
        ResponseEntity<?> response = cartController.removeCartItem(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Item removed from cart successfully", response.getBody());
        verify(cartService).removeCartItem(1L);
    }

    @Test
    void testClearCart_Success() {
        // Arrange
        doNothing().when(cartService).clearUserCart(1L);

        // Act
        ResponseEntity<?> response = cartController.clearCart(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Cart cleared successfully", response.getBody());
        verify(cartService).clearUserCart(1L);
    }

    @Test
    void testGetCartTotal_Success() {
        // Arrange
        when(cartService.calculateCartTotal(1L)).thenReturn(21.98);

        // Act
        ResponseEntity<?> response = cartController.getCartTotal(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(21.98, response.getBody());
        verify(cartService).calculateCartTotal(1L);
    }

    @Test
    void testGetCartItemCount_Success() {
        // Arrange
        when(cartService.getCartItemCount(1L)).thenReturn(2);

        // Act
        ResponseEntity<?> response = cartController.getCartItemCount(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody());
        verify(cartService).getCartItemCount(1L);
    }

    @Test
    void testAddCartItem_Error() {
        // Arrange
        CartController.CartItemRequest request = new CartController.CartItemRequest();
        request.setUserId(1L);
        request.setProductId(1L);
        request.setQuantity(2);
        
        when(cartService.addToCart(1L, 1L, 2))
            .thenThrow(new RuntimeException("Product not found"));

        // Act
        ResponseEntity<?> response = cartController.addCartItem(request);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        Object responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.toString().contains("Error adding to cart"));
        verify(cartService).addToCart(1L, 1L, 2);
    }

    @Test
    void testCartItemRequest_GettersAndSetters() {
        // Arrange
        CartController.CartItemRequest request = new CartController.CartItemRequest();

        // Act
        request.setUserId(1L);
        request.setProductId(2L);
        request.setQuantity(3);

        // Assert
        assertEquals(1L, request.getUserId());
        assertEquals(2L, request.getProductId());
        assertEquals(3, request.getQuantity());
    }
}
