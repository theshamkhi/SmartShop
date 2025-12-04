package com.smartshop.smartshop.model.entity;

import com.smartshop.smartshop.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal sousTotal;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal remise = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tva;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalTTC;

    private String codePromo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus statut = OrderStatus.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantRestant;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Paiement> paiements = new ArrayList<>();

}