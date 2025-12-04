package com.smartshop.smartshop.service;

import com.smartshop.smartshop.model.dto.request.CommandeRequest;
import com.smartshop.smartshop.model.dto.request.OrderItemRequest;
import com.smartshop.smartshop.model.dto.response.CommandeResponse;
import com.smartshop.smartshop.exception.BusinessRuleException;
import com.smartshop.smartshop.exception.ResourceNotFoundException;
import com.smartshop.smartshop.mapper.CommandeMapper;
import com.smartshop.smartshop.model.entity.Client;
import com.smartshop.smartshop.model.entity.Commande;
import com.smartshop.smartshop.model.entity.OrderItem;
import com.smartshop.smartshop.model.entity.Product;
import com.smartshop.smartshop.model.enums.OrderStatus;
import com.smartshop.smartshop.repository.ClientRepository;
import com.smartshop.smartshop.repository.CommandeRepository;
import com.smartshop.smartshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final CommandeMapper commandeMapper;
    private final LoyaltyService loyaltyService;
    private final ClientService clientService;
    private final ProductService productService;

    private static final BigDecimal TVA_RATE = new BigDecimal("0.20");
    private static final BigDecimal PROMO_DISCOUNT = new BigDecimal("0.10");
    private static final Pattern PROMO_PATTERN = Pattern.compile("^PROMO-[A-Z0-9]{4}$");

    @Transactional
    public CommandeResponse createCommande(CommandeRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + request.getClientId()));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessRuleException("Order must contain at least one item");
        }

        validateStock(request.getItems());

        Commande commande = new Commande();
        commande.setClient(client);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal sousTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findByIdAndIsDeletedFalse(itemRequest.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProduitId()));

            BigDecimal totalLigne = product.getPrixUnitaire()
                    .multiply(new BigDecimal(itemRequest.getQuantite()))
                    .setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = OrderItem.builder()
                    .commande(commande)
                    .produit(product)
                    .quantite(itemRequest.getQuantite())
                    .prixUnitaire(product.getPrixUnitaire())
                    .totalLigne(totalLigne)
                    .build();

            orderItems.add(orderItem);
            sousTotal = sousTotal.add(totalLigne);
        }

        commande.setOrderItems(orderItems);
        commande.setSousTotal(sousTotal.setScale(2, RoundingMode.HALF_UP));

        BigDecimal remiseTotale = calculateDiscount(client, sousTotal, request.getCodePromo());
        commande.setRemise(remiseTotale);
        commande.setCodePromo(request.getCodePromo());

        BigDecimal montantApresRemise = sousTotal.subtract(remiseTotale);
        BigDecimal tva = montantApresRemise.multiply(TVA_RATE).setScale(2, RoundingMode.HALF_UP);
        commande.setTva(tva);

        BigDecimal totalTTC = montantApresRemise.add(tva).setScale(2, RoundingMode.HALF_UP);
        commande.setTotalTTC(totalTTC);
        commande.setMontantRestant(totalTTC);

        Commande saved = commandeRepository.save(commande);
        return commandeMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CommandeResponse getCommandeById(String id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with id: " + id));
        return commandeMapper.toResponse(commande);
    }

    @Transactional(readOnly = true)
    public List<CommandeResponse> getAllCommandes() {
        return commandeRepository.findAll().stream()
                .map(commandeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommandeResponse> getCommandesByClientId(String clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client not found with id: " + clientId);
        }

        return commandeRepository.findByClientId(clientId).stream()
                .map(commandeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommandeResponse confirmCommande(String id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with id: " + id));

        if (commande.getStatut() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING orders can be confirmed");
        }

        if (commande.getMontantRestant().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessRuleException("Order must be fully paid before confirmation. Remaining amount: " + commande.getMontantRestant());
        }

        commande.setStatut(OrderStatus.CONFIRMED);

        for (OrderItem item : commande.getOrderItems()) {
            productService.decrementStock(item.getProduit().getId(), item.getQuantite());
        }

        clientService.updateClientStatistics(commande.getClient().getId(), commande.getTotalTTC());

        Commande updated = commandeRepository.save(commande);
        return commandeMapper.toResponse(updated);
    }

    @Transactional
    public CommandeResponse cancelCommande(String id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with id: " + id));

        if (commande.getStatut() == OrderStatus.CONFIRMED) {
            throw new BusinessRuleException("Confirmed orders cannot be canceled");
        }

        commande.setStatut(OrderStatus.CANCELED);
        Commande updated = commandeRepository.save(commande);
        return commandeMapper.toResponse(updated);
    }

    @Transactional
    public CommandeResponse rejectCommande(String id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with id: " + id));

        if (commande.getStatut() == OrderStatus.CONFIRMED) {
            throw new BusinessRuleException("Confirmed orders cannot be rejected");
        }

        commande.setStatut(OrderStatus.REJECTED);
        Commande updated = commandeRepository.save(commande);
        return commandeMapper.toResponse(updated);
    }

    private void validateStock(List<OrderItemRequest> items) {
        for (OrderItemRequest item : items) {
            Product product = productRepository.findByIdAndIsDeletedFalse(item.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProduitId()));

            if (item.getQuantite() > product.getStockDisponible()) {
                throw new BusinessRuleException(
                        String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d",
                                product.getNom(), item.getQuantite(), product.getStockDisponible())
                );
            }
        }
    }

    private BigDecimal calculateDiscount(Client client, BigDecimal sousTotal, String codePromo) {
        BigDecimal remise = BigDecimal.ZERO;

        if (codePromo != null && !codePromo.isEmpty()) {
            if (!PROMO_PATTERN.matcher(codePromo).matches()) {
                throw new BusinessRuleException("Invalid promo code format. Expected format: PROMO-XXXX");
            }
            remise = sousTotal.multiply(PROMO_DISCOUNT);
        }

        BigDecimal loyaltyDiscountRate = loyaltyService.getLoyaltyDiscount(client.getNiveauFidelite(), sousTotal);
        BigDecimal loyaltyDiscount = sousTotal.multiply(loyaltyDiscountRate);

        remise = remise.add(loyaltyDiscount);
        return remise.setScale(2, RoundingMode.HALF_UP);
    }
}