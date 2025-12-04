package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.request.ClientRequest;
import com.smartshop.smartshop.model.dto.response.ClientResponse;
import com.smartshop.smartshop.exception.UnauthorizedException;
import com.smartshop.smartshop.model.entity.User;
import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.service.AuthService;
import com.smartshop.smartshop.service.ClientService;
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
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Client management endpoints")
public class ClientController {

    private final ClientService clientService;
    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Create new client (ADMIN only)")
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody ClientRequest request,
            HttpSession session) {
        checkAdminAccess(session);
        ClientResponse response = clientService.createClient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get client by ID")
    public ResponseEntity<ClientResponse> getClientById(
            @PathVariable String id,
            HttpSession session) {
        User currentUser = authService.getCurrentUser(session);

        if (currentUser.getRole() == UserRole.CLIENT && !currentUser.getId().equals(id)) {
            throw new UnauthorizedException("Clients can only access their own data");
        }

        ClientResponse response = clientService.getClientById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all clients (ADMIN only)")
    public ResponseEntity<List<ClientResponse>> getAllClients(HttpSession session) {
        checkAdminAccess(session);
        List<ClientResponse> responses = clientService.getAllClients();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update client (ADMIN only)")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable String id,
            @Valid @RequestBody ClientRequest request,
            HttpSession session) {
        checkAdminAccess(session);
        ClientResponse response = clientService.updateClient(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete client (ADMIN only)")
    public ResponseEntity<Map<String, String>> deleteClient(
            @PathVariable String id,
            HttpSession session) {
        checkAdminAccess(session);
        clientService.deleteClient(id);
        return ResponseEntity.ok(Map.of("message", "Client deleted successfully"));
    }

    private void checkAdminAccess(HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }
}