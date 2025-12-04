package com.smartshop.smartshop.service;

import com.smartshop.smartshop.model.dto.request.PaiementRequest;
import com.smartshop.smartshop.model.dto.response.PaiementResponse;
import com.smartshop.smartshop.exception.BusinessRuleException;
import com.smartshop.smartshop.exception.ResourceNotFoundException;
import com.smartshop.smartshop.mapper.PaiementMapper;
import com.smartshop.smartshop.model.entity.Commande;
import com.smartshop.smartshop.model.entity.Paiement;
import com.smartshop.smartshop.model.enums.OrderStatus;
import com.smartshop.smartshop.model.enums.PaymentType;
import com.smartshop.smartshop.repository.CommandeRepository;
import com.smartshop.smartshop.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final CommandeRepository commandeRepository;
    private final PaiementMapper paiementMapper;

    private static final BigDecimal MAX_CASH_AMOUNT = new BigDecimal("20000.00");

    @Transactional
    public PaiementResponse createPaiement(PaiementRequest request) {
        Commande commande = commandeRepository.findById(request.getCommandeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with id: " + request.getCommandeId()));

        if (commande.getStatut() == OrderStatus.CONFIRMED) {
            throw new BusinessRuleException("Cannot add payment to confirmed order");
        }

        if (commande.getStatut() == OrderStatus.CANCELED || commande.getStatut() == OrderStatus.REJECTED) {
            throw new BusinessRuleException("Cannot add payment to canceled or rejected order");
        }

        if (request.getTypePaiement() == PaymentType.ESPECES &&
                request.getMontant().compareTo(MAX_CASH_AMOUNT) > 0) {
            throw new BusinessRuleException("Cash payments cannot exceed 20,000 DH. Amount: " + request.getMontant());
        }

        if (request.getMontant().compareTo(commande.getMontantRestant()) > 0) {
            throw new BusinessRuleException(
                    String.format("Payment amount (%s) exceeds remaining amount (%s)",
                            request.getMontant(), commande.getMontantRestant())
            );
        }

        long paymentCount = paiementRepository.countByCommandeId(commande.getId());
        String numeroPaiement = String.format("PAY-%s-%03d", commande.getId().substring(0, 8), paymentCount + 1);

        Paiement paiement = Paiement.builder()
                .commande(commande)
                .numeroPaiement(numeroPaiement)
                .montant(request.getMontant())
                .typePaiement(request.getTypePaiement())
                .reference(request.getReference())
                .banque(request.getBanque())
                .echeance(request.getEcheance())
                .build();

        Paiement saved = paiementRepository.save(paiement);

        commande.setMontantRestant(commande.getMontantRestant().subtract(request.getMontant()));
        commandeRepository.save(commande);

        return paiementMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaiementResponse getPaiementById(String id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement not found with id: " + id));
        return paiementMapper.toResponse(paiement);
    }

    @Transactional(readOnly = true)
    public List<PaiementResponse> getAllPaiements() {
        return paiementRepository.findAll().stream()
                .map(paiementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePaiement(String id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement not found with id: " + id));

        Commande commande = paiement.getCommande();
        if (commande.getStatut() == OrderStatus.CONFIRMED) {
            throw new BusinessRuleException("Cannot delete payment from confirmed order");
        }

        commande.setMontantRestant(commande.getMontantRestant().add(paiement.getMontant()));
        commandeRepository.save(commande);

        paiementRepository.delete(paiement);
    }
}