package com.smartshop.smartshop.model.dto.response;

import com.smartshop.smartshop.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeResponse {
    private String id;
    private String clientId;
    private String clientNom;
    private LocalDateTime dateCreation;
    private BigDecimal sousTotal;
    private BigDecimal remise;
    private BigDecimal tva;
    private BigDecimal totalTTC;
    private String codePromo;
    private OrderStatus statut;
    private BigDecimal montantRestant;
    private List<OrderItemResponse> items;
}
