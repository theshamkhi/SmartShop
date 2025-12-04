package com.smartshop.smartshop.service;

import com.smartshop.smartshop.model.dto.request.ClientRequest;
import com.smartshop.smartshop.model.dto.response.ClientResponse;
import com.smartshop.smartshop.exception.BusinessRuleException;
import com.smartshop.smartshop.exception.ResourceNotFoundException;
import com.smartshop.smartshop.mapper.ClientMapper;
import com.smartshop.smartshop.model.entity.Client;
import com.smartshop.smartshop.model.enums.CustomerTier;
import com.smartshop.smartshop.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final LoyaltyService loyaltyService;

    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email already exists: " + request.getEmail());
        }

        if (clientRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessRuleException("Username already exists: " + request.getUsername());
        }

        Client client = clientMapper.toEntity(request);
        Client saved = clientRepository.save(client);
        return clientMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(String id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return clientMapper.toResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClientResponse updateClient(String id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        if (!client.getEmail().equals(request.getEmail()) &&
                clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email already exists: " + request.getEmail());
        }

        clientMapper.updateEntity(request, client);
        Client updated = clientRepository.save(client);
        return clientMapper.toResponse(updated);
    }

    @Transactional
    public void deleteClient(String id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }

    @Transactional
    public void updateClientStatistics(String clientId, BigDecimal orderTotal) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent().add(orderTotal));

        if (client.getDateFirstOrder() == null) {
            client.setDateFirstOrder(LocalDate.now());
        }
        client.setDateLastOrder(LocalDate.now());

        CustomerTier newTier = loyaltyService.calculateTier(client);
        client.setNiveauFidelite(newTier);

        clientRepository.save(client);
    }
}