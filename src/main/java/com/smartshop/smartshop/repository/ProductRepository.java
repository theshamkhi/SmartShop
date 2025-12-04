package com.smartshop.smartshop.repository;

import com.smartshop.smartshop.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByIsDeletedFalse();
    Optional<Product> findByIdAndIsDeletedFalse(String id);
}