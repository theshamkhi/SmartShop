package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.request.CommandeRequest;
import com.smartshop.smartshop.model.dto.response.CommandeResponse;
import com.smartshop.smartshop.exception.UnauthorizedException;
import com.smartshop.smartshop.model.entity.User;
import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.service.AuthService;
import com.smartshop.smartshop.service.CommandeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commandes")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
public class CommandeController {

    private final CommandeService commandeService;
    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Create new order")
    public ResponseEntity<CommandeResponse> createCommande(
            @Valid @RequestBody CommandeRequest request,
            HttpSession session) {
        User currentUser = authService.getCurrentUser(session);

        if (currentUser.getRole() == UserRole.CLIENT && !currentUser.getId().equals(request.getClientId())) {
            throw new UnauthorizedException("Clients can only create orders for themselves");
        }

        CommandeResponse response = commandeService.createCommande(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<CommandeResponse> getCommandeById(
            @PathVariable String id,
            HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        CommandeResponse response = commandeService.getCommandeById(id);

        if (currentUser.getRole() == UserRole.CLIENT && !currentUser.getId().equals(response.getClientId())) {
            throw new UnauthorizedException("Clients can only access their own orders");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<List<CommandeResponse>> getAllCommandes(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);

        if (currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.ok(commandeService.getAllCommandes());
        } else {
            return ResponseEntity.ok(commandeService.getCommandesByClientId(currentUser.getId()));
        }
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get orders by client ID")
    public ResponseEntity<List<CommandeResponse>> getCommandesByClientId(
            @PathVariable String clientId,
            HttpSession session) {
        User currentUser = authService.getCurrentUser(session);

        if (currentUser.getRole() == UserRole.CLIENT && !currentUser.getId().equals(clientId)) {
            throw new UnauthorizedException("Clients can only access their own orders");
        }

        List<CommandeResponse> responses = commandeService.getCommandesByClientId(clientId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirm order (ADMIN only)")
    public ResponseEntity<CommandeResponse> confirmCommande(
            @PathVariable String id,
            HttpSession session) {
        checkAdminAccess(session);
        CommandeResponse response = commandeService.confirmCommande(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel order")
    public ResponseEntity<CommandeResponse> cancelCommande(
            @PathVariable String id,
            HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        CommandeResponse commande = commandeService.getCommandeById(id);

        if (currentUser.getRole() == UserRole.CLIENT && !currentUser.getId().equals(commande.getClientId())) {
            throw new UnauthorizedException("Clients can only cancel their own orders");
        }

        CommandeResponse response = commandeService.cancelCommande(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject order (ADMIN only)")
    public ResponseEntity<CommandeResponse> rejectCommande(
            @PathVariable String id,
            HttpSession session) {
        checkAdminAccess(session);
        CommandeResponse response = commandeService.rejectCommande(id);
        return ResponseEntity.ok(response);
    }

    private void checkAdminAccess(HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }
}