package com.smartshop.smartshop.mapper;

import com.smartshop.smartshop.model.dto.request.ClientRequest;
import com.smartshop.smartshop.model.dto.response.ClientResponse;
import com.smartshop.smartshop.model.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "CLIENT")
    @Mapping(target = "niveauFidelite", constant = "BASIC")
    @Mapping(target = "totalOrders", constant = "0")
    @Mapping(target = "totalSpent", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "dateFirstOrder", ignore = true)
    @Mapping(target = "dateLastOrder", ignore = true)
    @Mapping(target = "commandes", ignore = true)
    Client toEntity(ClientRequest request);

    ClientResponse toResponse(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "niveauFidelite", ignore = true)
    @Mapping(target = "totalOrders", ignore = true)
    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "dateFirstOrder", ignore = true)
    @Mapping(target = "dateLastOrder", ignore = true)
    @Mapping(target = "commandes", ignore = true)
    void updateEntity(ClientRequest request, @MappingTarget Client client);
}