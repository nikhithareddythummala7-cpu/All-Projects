package com.example.floweandgift.service;

import com.example.floweandgift.model.Coupon;
import com.example.floweandgift.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private Coupon validCoupon;
    private Coupon expiredCoupon;
    private Coupon usedUpCoupon;

    @BeforeEach
    void setUp() {
        validCoupon = new Coupon();
        validCoupon.setId("1");
        validCoupon.setCode("VALID10");
        validCoupon.setType(Coupon.CouponType.PERCENTAGE);
        validCoupon.setDiscountValue(10.0);
        validCoupon.setMinimumCartValue(50.0);
        validCoupon.setExpiryDate(LocalDateTime.now().plusDays(1));
        validCoupon.setActive(true);
        validCoupon.setUsageLimit(10);
        validCoupon.setUsedCount(0);

        expiredCoupon = new Coupon();
        expiredCoupon.setId("2");
        expiredCoupon.setCode("EXPIRED");
        expiredCoupon.setType(Coupon.CouponType.FIXED);
        expiredCoupon.setDiscountValue(5.0);
        expiredCoupon.setExpiryDate(LocalDateTime.now().minusDays(1));
        expiredCoupon.setActive(true);
        expiredCoupon.setUsageLimit(10);
        expiredCoupon.setUsedCount(0);

        usedUpCoupon = new Coupon();
        usedUpCoupon.setId("3");
        usedUpCoupon.setCode("USEDUP");
        usedUpCoupon.setType(Coupon.CouponType.FIXED);
        usedUpCoupon.setDiscountValue(5.0);
        usedUpCoupon.setExpiryDate(LocalDateTime.now().plusDays(1));
        usedUpCoupon.setActive(true);
        usedUpCoupon.setUsageLimit(1);
        usedUpCoupon.setUsedCount(1);
    }

    @Test
    void testCreateCoupon() {
        when(couponRepository.save(any(Coupon.class))).thenReturn(validCoupon);

        Coupon result = couponService.createCoupon(validCoupon);

        assertNotNull(result);
        assertEquals("VALID10", result.getCode());
        verify(couponRepository, times(1)).save(validCoupon);
    }

    @Test
    void testGetCouponById() {
        when(couponRepository.findById("1")).thenReturn(Optional.of(validCoupon));

        Optional<Coupon> result = couponService.getCouponById("1");

        assertTrue(result.isPresent());
        assertEquals("VALID10", result.get().getCode());
    }

    @Test
    void testGetCouponByCode() {
        when(couponRepository.findByCode("VALID10")).thenReturn(Optional.of(validCoupon));

        Optional<Coupon> result = couponService.getCouponByCode("VALID10");

        assertTrue(result.isPresent());
        assertEquals("VALID10", result.get().getCode());
    }

    @Test
    void testGetAllCoupons() {
        List<Coupon> coupons = Arrays.asList(validCoupon, expiredCoupon);
        when(couponRepository.findAll()).thenReturn(coupons);

        List<Coupon> result = couponService.getAllCoupons();

        assertEquals(2, result.size());
        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void testUpdateCoupon() {
        when(couponRepository.save(any(Coupon.class))).thenReturn(validCoupon);

        Coupon result = couponService.updateCoupon(validCoupon);

        assertNotNull(result);
        verify(couponRepository, times(1)).save(validCoupon);
    }

    @Test
    void testDeleteCoupon() {
        doNothing().when(couponRepository).deleteById("1");

        couponService.deleteCoupon("1");

        verify(couponRepository, times(1)).deleteById("1");
    }

    @Test
    void testApplyCoupon_ValidCoupon() {
        when(couponRepository.findByCode("VALID10")).thenReturn(Optional.of(validCoupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(validCoupon);

        double discount = couponService.applyCoupon("VALID10", 100.0);

        assertEquals(10.0, discount); // 10% of 100
        verify(couponRepository, times(1)).save(validCoupon);
        assertEquals(1, validCoupon.getUsedCount());
    }

    @Test
    void testApplyCoupon_InvalidCode() {
        when(couponRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        double discount = couponService.applyCoupon("INVALID", 100.0);

        assertEquals(0.0, discount);
    }

    @Test
    void testApplyCoupon_ExpiredCoupon() {
        when(couponRepository.findByCode("EXPIRED")).thenReturn(Optional.of(expiredCoupon));

        double discount = couponService.applyCoupon("EXPIRED", 100.0);

        assertEquals(0.0, discount);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void testApplyCoupon_UsedUpCoupon() {
        when(couponRepository.findByCode("USEDUP")).thenReturn(Optional.of(usedUpCoupon));

        double discount = couponService.applyCoupon("USEDUP", 100.0);

        assertEquals(0.0, discount);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void testApplyCoupon_BelowMinimumCartValue() {
        when(couponRepository.findByCode("VALID10")).thenReturn(Optional.of(validCoupon));

        double discount = couponService.applyCoupon("VALID10", 30.0); // Below minimum 50

        assertEquals(0.0, discount);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void testApplyCoupon_FixedDiscount() {
        Coupon fixedCoupon = new Coupon();
        fixedCoupon.setId("4");
        fixedCoupon.setCode("FIXED5");
        fixedCoupon.setType(Coupon.CouponType.FIXED);
        fixedCoupon.setDiscountValue(5.0);
        fixedCoupon.setMinimumCartValue(10.0);
        fixedCoupon.setExpiryDate(LocalDateTime.now().plusDays(1));
        fixedCoupon.setActive(true);
        fixedCoupon.setUsageLimit(10);
        fixedCoupon.setUsedCount(0);

        when(couponRepository.findByCode("FIXED5")).thenReturn(Optional.of(fixedCoupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(fixedCoupon);

        double discount = couponService.applyCoupon("FIXED5", 20.0);

        assertEquals(5.0, discount);
        verify(couponRepository, times(1)).save(fixedCoupon);
    }

    @Test
    void testValidateCoupon_Valid() {
        when(couponRepository.findByCode("VALID10")).thenReturn(Optional.of(validCoupon));

        boolean isValid = couponService.validateCoupon("VALID10", 100.0);

        assertTrue(isValid);
    }

    @Test
    void testValidateCoupon_InvalidCode() {
        when(couponRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        boolean isValid = couponService.validateCoupon("INVALID", 100.0);

        assertFalse(isValid);
    }

    @Test
    void testValidateCoupon_BelowMinimum() {
        when(couponRepository.findByCode("VALID10")).thenReturn(Optional.of(validCoupon));

        boolean isValid = couponService.validateCoupon("VALID10", 30.0);

        assertFalse(isValid);
    }
}
