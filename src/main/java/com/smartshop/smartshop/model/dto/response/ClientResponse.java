package com.smartshop.smartshop.model.dto.response;

import com.smartshop.smartshop.model.enums.CustomerTier;
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
public class ClientResponse {
    private String id;
    private String username;
    private String nom;
    private String email;
    private CustomerTier niveauFidelite;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private LocalDate dateFirstOrder;
    private LocalDate dateLastOrder;
}
