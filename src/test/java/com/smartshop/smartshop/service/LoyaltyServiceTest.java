package com.smartshop.smartshop.service;

import com.smartshop.smartshop.model.entity.Client;
import com.smartshop.smartshop.model.enums.CustomerTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoyaltyServiceTest {

    private LoyaltyService loyaltyService;

    @BeforeEach
    void setUp() {
        loyaltyService = new LoyaltyService();
    }

    @Test
    void testCalculateTier_Basic() {
        Client client = Client.builder()
                .totalOrders(3)
                .totalSpent(new BigDecimal("1000.00"))
                .build();

        CustomerTier tier = loyaltyService.calculateTier(client);
        assertEquals(CustomerTier.BASIC, tier);
    }

    @Test
    void testCalculateTier_Silver_ByOrders() {
        Client client = Client.builder()
                .totalOrders(5)
                .totalSpent(new BigDecimal("1000.00"))
                .build();

        CustomerTier tier = loyaltyService.calculateTier(client);
        assertEquals(CustomerTier.SILVER, tier);
    }

    @Test
    void testCalculateTier_Silver_ByAmount() {
        Client client = Client.builder()
                .totalOrders(3)
                .totalSpent(new BigDecimal("2000.00"))
                .build();

        CustomerTier tier = loyaltyService.calculateTier(client);
        assertEquals(CustomerTier.SILVER, tier);
    }

    @Test
    void testCalculateTier_Gold_ByOrders() {
        Client client = Client.builder()
                .totalOrders(10)
                .totalSpent(new BigDecimal("3000.00"))
                .build();

        CustomerTier tier = loyaltyService.calculateTier(client);
        assertEquals(CustomerTier.GOLD, tier);
    }

    @Test
    void testCalculateTier_Gold_ByAmount() {
        Client client = Client.builder()
                .totalOrders(7)
                .totalSpent(new BigDecimal("5000.00"))
                .build();

        CustomerTier tier = loyaltyService.calculateTier(client);
        assertEquals(CustomerTier.GOLD, tier);
    }

    @Test
    void testCalculateTier_Platinum_ByOrders() {
        Client client = Client.builder()
                .totalOrders(20)
                .totalSpent(new BigDecimal("8000.00"))
                .build();

        CustomerTier tier = loyaltyService.calculateTier(client);
        assertEquals(CustomerTier.PLATINUM, tier);
    }

    @Test
    void testCalculateTier_Platinum_ByAmount() {
        Client client = Client.builder()
                .totalOrders(15)
                .totalSpent(new BigDecimal("10000.00"))
                .build();

        CustomerTier tier = loyaltyService.calculateTier(client);
        assertEquals(CustomerTier.PLATINUM, tier);
    }

    @Test
    void testGetLoyaltyDiscount_Basic_NoDiscount() {
        BigDecimal sousTotal = new BigDecimal("600.00");
        BigDecimal discount = loyaltyService.getLoyaltyDiscount(CustomerTier.BASIC, sousTotal);
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void testGetLoyaltyDiscount_BelowMinimum() {
        BigDecimal sousTotal = new BigDecimal("400.00");
        BigDecimal discount = loyaltyService.getLoyaltyDiscount(CustomerTier.SILVER, sousTotal);
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void testGetLoyaltyDiscount_Silver() {
        BigDecimal sousTotal = new BigDecimal("1000.00");
        BigDecimal discount = loyaltyService.getLoyaltyDiscount(CustomerTier.SILVER, sousTotal);
        assertEquals(new BigDecimal("0.05"), discount);
    }

    @Test
    void testGetLoyaltyDiscount_Gold() {
        BigDecimal sousTotal = new BigDecimal("1000.00");
        BigDecimal discount = loyaltyService.getLoyaltyDiscount(CustomerTier.GOLD, sousTotal);
        assertEquals(new BigDecimal("0.10"), discount);
    }

    @Test
    void testGetLoyaltyDiscount_Platinum() {
        BigDecimal sousTotal = new BigDecimal("1000.00");
        BigDecimal discount = loyaltyService.getLoyaltyDiscount(CustomerTier.PLATINUM, sousTotal);
        assertEquals(new BigDecimal("0.15"), discount);
    }
}