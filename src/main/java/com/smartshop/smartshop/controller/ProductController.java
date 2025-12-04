package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.request.ProductRequest;
import com.smartshop.smartshop.model.dto.response.ProductResponse;
import com.smartshop.smartshop.exception.UnauthorizedException;
import com.smartshop.smartshop.model.entity.User;
import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.service.AuthService;
import com.smartshop.smartshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Create new product (ADMIN only)")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            HttpSession session) {
        checkAdminAccess(session);
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable String id,
            HttpSession session) {
        authService.getCurrentUser(session);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ProductResponse>> getAllProducts(HttpSession session) {
        authService.getCurrentUser(session);
        List<ProductResponse> responses = productService.getAllProducts();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product (ADMIN only)")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request,
            HttpSession session) {
        checkAdminAccess(session);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product (soft delete, ADMIN only)")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @PathVariable String id,
            HttpSession session) {
        checkAdminAccess(session);
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    private void checkAdminAccess(HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }
}