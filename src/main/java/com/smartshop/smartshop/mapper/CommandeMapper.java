package com.smartshop.smartshop.mapper;

import com.smartshop.smartshop.model.dto.response.CommandeResponse;
import com.smartshop.smartshop.model.entity.Commande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface CommandeMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.nom", target = "clientNom")
    @Mapping(source = "orderItems", target = "items")
    CommandeResponse toResponse(Commande commande);
}
