package com.example.floweandgift.service;

import com.example.floweandgift.model.Coupon;
import com.example.floweandgift.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public Optional<Coupon> getCouponById(String id) {
        return couponRepository.findById(id);
    }

    public Optional<Coupon> getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon updateCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public void deleteCoupon(String id) {
        couponRepository.deleteById(id);
    }

    public double applyCoupon(String code, double cartTotal) {
        Optional<Coupon> couponOpt = getCouponByCode(code);
        if (couponOpt.isPresent()) {
            Coupon coupon = couponOpt.get();
            if (coupon.isValid() && cartTotal >= coupon.getMinimumCartValue()) {
                double discount = coupon.calculateDiscount(cartTotal);
                coupon.setUsedCount(coupon.getUsedCount() + 1);
                updateCoupon(coupon);
                return discount;
            }
        }
        return 0.0;
    }

    public boolean validateCoupon(String code, double cartTotal) {
        Optional<Coupon> couponOpt = getCouponByCode(code);
        return couponOpt.isPresent() && couponOpt.get().isValid() && cartTotal >= couponOpt.get().getMinimumCartValue();
    }
}
