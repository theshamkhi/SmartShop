package com.smartshop.smartshop.repository;

import com.smartshop.smartshop.model.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, String> {
    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.commande.id = :commandeId")
    long countByCommandeId(@Param("commandeId") String commandeId);
}