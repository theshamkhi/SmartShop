package com.smartshop.smartshop.repository;

import com.smartshop.smartshop.model.entity.Commande;
import com.smartshop.smartshop.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, String> {
    List<Commande> findByClient(Client client);
    List<Commande> findByClientId(String clientId);
}