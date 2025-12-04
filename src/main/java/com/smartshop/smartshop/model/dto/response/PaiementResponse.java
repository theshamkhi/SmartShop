package com.smartshop.smartshop.model.dto.response;

import com.smartshop.smartshop.model.enums.PaymentStatus;
import com.smartshop.smartshop.model.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementResponse {
    private String id;
    private String commandeId;
    private String numeroPaiement;
    private BigDecimal montant;
    private PaymentType typePaiement;
    private LocalDateTime datePaiement;
    private LocalDate dateEncaissement;
    private PaymentStatus statut;
    private String reference;
    private String banque;
    private LocalDate echeance;
}
