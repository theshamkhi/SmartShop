package com.smartshop.smartshop.service;

import com.smartshop.smartshop.model.dto.request.CommandeRequest;
import com.smartshop.smartshop.model.dto.request.OrderItemRequest;
import com.smartshop.smartshop.model.dto.response.CommandeResponse;
import com.smartshop.smartshop.exception.BusinessRuleException;
import com.smartshop.smartshop.exception.ResourceNotFoundException;
import com.smartshop.smartshop.mapper.CommandeMapper;
import com.smartshop.smartshop.model.entity.Client;
import com.smartshop.smartshop.model.entity.Commande;
import com.smartshop.smartshop.model.entity.Product;
import com.smartshop.smartshop.model.enums.CustomerTier;
import com.smartshop.smartshop.model.enums.OrderStatus;
import com.smartshop.smartshop.repository.ClientRepository;
import com.smartshop.smartshop.repository.CommandeRepository;
import com.smartshop.smartshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CommandeMapper commandeMapper;

    @Mock
    private LoyaltyService loyaltyService;

    @Mock
    private ClientService clientService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CommandeService commandeService;

    private Client testClient;
    private Product testProduct;
    private CommandeRequest commandeRequest;

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
                .id("client1")
                .nom("Test Client")
                .email("test@example.com")
                .niveauFidelite(CustomerTier.BASIC)
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .build();

        testProduct = Product.builder()
                .id("product1")
                .nom("Test Product")
                .prixUnitaire(new BigDecimal("100.00"))
                .stockDisponible(10)
                .isDeleted(false)
                .build();

        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .produitId("product1")
                .quantite(2)
                .build();

        commandeRequest = CommandeRequest.builder()
                .clientId("client1")
                .items(Arrays.asList(itemRequest))
                .build();
    }

    @Test
    void testCreateCommande_Success() {
        when(clientRepository.findById("client1")).thenReturn(Optional.of(testClient));
        when(productRepository.findByIdAndIsDeletedFalse("product1")).thenReturn(Optional.of(testProduct));
        when(loyaltyService.getLoyaltyDiscount(any(), any())).thenReturn(BigDecimal.ZERO);
        when(commandeRepository.save(any(Commande.class))).thenAnswer(i -> i.getArguments()[0]);
        when(commandeMapper.toResponse(any())).thenReturn(new CommandeResponse());

        CommandeResponse response = commandeService.createCommande(commandeRequest);

        assertNotNull(response);
        verify(commandeRepository, times(1)).save(any(Commande.class));
    }

    @Test
    void testCreateCommande_ClientNotFound() {
        when(clientRepository.findById("client1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            commandeService.createCommande(commandeRequest);
        });
    }

    @Test
    void testCreateCommande_EmptyItems() {
        commandeRequest.setItems(Arrays.asList());
        when(clientRepository.findById("client1")).thenReturn(Optional.of(testClient));

        assertThrows(BusinessRuleException.class, () -> {
            commandeService.createCommande(commandeRequest);
        });
    }

    @Test
    void testCreateCommande_InsufficientStock() {
        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .produitId("product1")
                .quantite(15)
                .build();
        commandeRequest.setItems(Arrays.asList(itemRequest));

        when(clientRepository.findById("client1")).thenReturn(Optional.of(testClient));
        when(productRepository.findByIdAndIsDeletedFalse("product1")).thenReturn(Optional.of(testProduct));

        assertThrows(BusinessRuleException.class, () -> {
            commandeService.createCommande(commandeRequest);
        });
    }

    @Test
    void testCreateCommande_WithValidPromoCode() {
        commandeRequest.setCodePromo("PROMO-AB12");

        when(clientRepository.findById("client1")).thenReturn(Optional.of(testClient));
        when(productRepository.findByIdAndIsDeletedFalse("product1")).thenReturn(Optional.of(testProduct));
        when(loyaltyService.getLoyaltyDiscount(any(), any())).thenReturn(BigDecimal.ZERO);
        when(commandeRepository.save(any(Commande.class))).thenAnswer(i -> i.getArguments()[0]);
        when(commandeMapper.toResponse(any())).thenReturn(new CommandeResponse());

        CommandeResponse response = commandeService.createCommande(commandeRequest);

        assertNotNull(response);
        verify(commandeRepository, times(1)).save(any(Commande.class));
    }

    @Test
    void testCreateCommande_WithInvalidPromoCode() {
        commandeRequest.setCodePromo("INVALID");

        when(clientRepository.findById("client1")).thenReturn(Optional.of(testClient));
        when(productRepository.findByIdAndIsDeletedFalse("product1")).thenReturn(Optional.of(testProduct));

        assertThrows(BusinessRuleException.class, () -> {
            commandeService.createCommande(commandeRequest);
        });
    }

    @Test
    void testConfirmCommande_Success() {
        Commande commande = Commande.builder()
                .id("order1")
                .client(testClient)
                .statut(OrderStatus.PENDING)
                .montantRestant(BigDecimal.ZERO)
                .totalTTC(new BigDecimal("240.00"))
                .build();

        when(commandeRepository.findById("order1")).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(i -> i.getArguments()[0]);
        when(commandeMapper.toResponse(any())).thenReturn(new CommandeResponse());

        CommandeResponse response = commandeService.confirmCommande("order1");

        assertNotNull(response);
        verify(clientService, times(1)).updateClientStatistics(any(), any());
    }

    @Test
    void testConfirmCommande_NotFullyPaid() {
        Commande commande = Commande.builder()
                .id("order1")
                .client(testClient)
                .statut(OrderStatus.PENDING)
                .montantRestant(new BigDecimal("100.00"))
                .build();

        when(commandeRepository.findById("order1")).thenReturn(Optional.of(commande));

        assertThrows(BusinessRuleException.class, () -> {
            commandeService.confirmCommande("order1");
        });
    }

    @Test
    void testConfirmCommande_AlreadyConfirmed() {
        Commande commande = Commande.builder()
                .id("order1")
                .statut(OrderStatus.CONFIRMED)
                .build();

        when(commandeRepository.findById("order1")).thenReturn(Optional.of(commande));

        assertThrows(BusinessRuleException.class, () -> {
            commandeService.confirmCommande("order1");
        });
    }
}