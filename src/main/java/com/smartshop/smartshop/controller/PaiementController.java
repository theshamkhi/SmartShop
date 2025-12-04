package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.request.PaiementRequest;
import com.smartshop.smartshop.model.dto.response.PaiementResponse;
import com.smartshop.smartshop.exception.UnauthorizedException;
import com.smartshop.smartshop.model.entity.User;
import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.service.AuthService;
import com.smartshop.smartshop.service.PaiementService;
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
@RequestMapping("/paiements")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management endpoints")
public class PaiementController {

    private final PaiementService paiementService;
    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Create new payment (ADMIN only)")
    public ResponseEntity<PaiementResponse> createPaiement(
            @Valid @RequestBody PaiementRequest request,
            HttpSession session) {
        checkAdminAccess(session);
        PaiementResponse response = paiementService.createPaiement(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID (ADMIN only)")
    public ResponseEntity<PaiementResponse> getPaiementById(
            @PathVariable String id,
            HttpSession session) {
        checkAdminAccess(session);
        PaiementResponse response = paiementService.getPaiementById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all payments (ADMIN only)")
    public ResponseEntity<List<PaiementResponse>> getAllPaiements(HttpSession session) {
        checkAdminAccess(session);
        List<PaiementResponse> responses = paiementService.getAllPaiements();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment (ADMIN only)")
    public ResponseEntity<Map<String, String>> deletePaiement(
            @PathVariable String id,
            HttpSession session) {
        checkAdminAccess(session);
        paiementService.deletePaiement(id);
        return ResponseEntity.ok(Map.of("message", "Payment deleted successfully"));
    }

    private void checkAdminAccess(HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }
}