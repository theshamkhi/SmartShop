package com.smartshop.smartshop.model.dto.request;

import com.smartshop.smartshop.model.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementRequest {
    @NotBlank(message = "Commande ID is required")
    private String commandeId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal montant;

    @NotNull(message = "Payment type is required")
    private PaymentType typePaiement;

    private String reference;
    private String banque;
    private LocalDate echeance;
}