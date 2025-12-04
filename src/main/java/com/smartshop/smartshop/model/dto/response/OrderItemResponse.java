package com.smartshop.smartshop.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    private String id;
    private String produitId;
    private String produitNom;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal totalLigne;
}
