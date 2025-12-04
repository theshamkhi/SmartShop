package com.smartshop.smartshop.service;

import com.smartshop.smartshop.model.entity.Client;
import com.smartshop.smartshop.model.enums.CustomerTier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LoyaltyService {

    private static final int SILVER_ORDERS = 5;
    private static final BigDecimal SILVER_AMOUNT = new BigDecimal("2000.00");

    private static final int GOLD_ORDERS = 10;
    private static final BigDecimal GOLD_AMOUNT = new BigDecimal("5000.00");

    private static final int PLATINUM_ORDERS = 20;
    private static final BigDecimal PLATINUM_AMOUNT = new BigDecimal("10000.00");

    public CustomerTier calculateTier(Client client) {
        Integer orders = client.getTotalOrders();
        BigDecimal spent = client.getTotalSpent();

        if (orders >= PLATINUM_ORDERS || spent.compareTo(PLATINUM_AMOUNT) >= 0) {
            return CustomerTier.PLATINUM;
        } else if (orders >= GOLD_ORDERS || spent.compareTo(GOLD_AMOUNT) >= 0) {
            return CustomerTier.GOLD;
        } else if (orders >= SILVER_ORDERS || spent.compareTo(SILVER_AMOUNT) >= 0) {
            return CustomerTier.SILVER;
        }
        return CustomerTier.BASIC;
    }

    public BigDecimal getLoyaltyDiscount(CustomerTier tier, BigDecimal sousTotal) {
        BigDecimal minAmount = new BigDecimal("500.00");

        if (sousTotal.compareTo(minAmount) < 0) {
            return BigDecimal.ZERO;
        }

        return switch (tier) {
            case SILVER -> new BigDecimal("0.05");  // 5%
            case GOLD -> new BigDecimal("0.10");    // 10%
            case PLATINUM -> new BigDecimal("0.15"); // 15%
            default -> BigDecimal.ZERO;
        };
    }
}