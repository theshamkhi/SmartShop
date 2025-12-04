//package com.smartshop.smartshop.config;
//
//import com.smartshop.smartshop.model.entity.Admin;
//import com.smartshop.smartshop.model.entity.Client;
//import com.smartshop.smartshop.model.entity.Product;
//import com.smartshop.smartshop.model.enums.CustomerTier;
//import com.smartshop.smartshop.model.enums.UserRole;
//import com.smartshop.smartshop.repository.AdminRepository;
//import com.smartshop.smartshop.repository.ClientRepository;
//import com.smartshop.smartshop.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DataInitializer implements CommandLineRunner {
//
//    private final AdminRepository adminRepository;
//    private final ClientRepository clientRepository;
//    private final ProductRepository productRepository;
//
//    @Override
//    public void run(String... args) {
//        log.info("Initializing demo data...");
//
//        // Create Admin
//        Admin admin = Admin.builder()
//                .username("admin")
//                .password("admin123")
//                .role(UserRole.ADMIN)
//                .build();
//        adminRepository.save(admin);
//        log.info("Created admin user: {}", admin.getUsername());
//
//        // Create Clients
//        Client client1 = Client.builder()
//                .username("client1")
//                .password("client123")
//                .role(UserRole.CLIENT)
//                .nom("Jean Dupont")
//                .email("jean.dupont@example.com")
//                .niveauFidelite(CustomerTier.BASIC)
//                .totalOrders(0)
//                .totalSpent(BigDecimal.ZERO)
//                .build();
//        clientRepository.save(client1);
//
//        Client client2 = Client.builder()
//                .username("client2")
//                .password("client123")
//                .role(UserRole.CLIENT)
//                .nom("Marie Martin")
//                .email("marie.martin@example.com")
//                .niveauFidelite(CustomerTier.SILVER)
//                .totalOrders(6)
//                .totalSpent(new BigDecimal("2500.00"))
//                .build();
//        clientRepository.save(client2);
//        log.info("Created {} clients", clientRepository.count());
//
//        // Create Products
//        Product[] products = {
//                Product.builder()
//                        .nom("Laptop Dell XPS 13")
//                        .prixUnitaire(new BigDecimal("12000.00"))
//                        .stockDisponible(15)
//                        .isDeleted(false)
//                        .build(),
//                Product.builder()
//                        .nom("iPhone 15 Pro")
//                        .prixUnitaire(new BigDecimal("14000.00"))
//                        .stockDisponible(25)
//                        .isDeleted(false)
//                        .build(),
//                Product.builder()
//                        .nom("Samsung Galaxy S24")
//                        .prixUnitaire(new BigDecimal("8500.00"))
//                        .stockDisponible(30)
//                        .isDeleted(false)
//                        .build(),
//                Product.builder()
//                        .nom("iPad Air")
//                        .prixUnitaire(new BigDecimal("6000.00"))
//                        .stockDisponible(20)
//                        .isDeleted(false)
//                        .build(),
//                Product.builder()
//                        .nom("Sony WH-1000XM5")
//                        .prixUnitaire(new BigDecimal("3500.00"))
//                        .stockDisponible(50)
//                        .isDeleted(false)
//                        .build(),
//                Product.builder()
//                        .nom("MacBook Pro 14")
//                        .prixUnitaire(new BigDecimal("25000.00"))
//                        .stockDisponible(10)
//                        .isDeleted(false)
//                        .build(),
//                Product.builder()
//                        .nom("Apple Watch Series 9")
//                        .prixUnitaire(new BigDecimal("4500.00"))
//                        .stockDisponible(40)
//                        .isDeleted(false)
//                        .build(),
//                Product.builder()
//                        .nom("Canon EOS R6")
//                        .prixUnitaire(new BigDecimal("18000.00"))
//                        .stockDisponible(8)
//                        .isDeleted(false)
//                        .build()
//        };
//
//        for (Product product : products) {
//            productRepository.save(product);
//        }
//        log.info("Created {} products", productRepository.count());
//
//        log.info("Demo data initialization completed!");
//        log.info("===================================");
//        log.info("Login credentials:");
//        log.info("Admin - username: admin, password: admin123");
//        log.info("Client 1 - username: client1, password: client123");
//        log.info("Client 2 - username: client2, password: client123");
//        log.info("===================================");
//    }
//}