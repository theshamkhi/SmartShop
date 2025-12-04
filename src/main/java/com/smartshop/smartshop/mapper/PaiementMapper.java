package com.smartshop.smartshop.mapper;

import com.smartshop.smartshop.model.dto.response.PaiementResponse;
import com.smartshop.smartshop.model.entity.Paiement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaiementMapper {

    @Mapping(source = "commande.id", target = "commandeId")
    PaiementResponse toResponse(Paiement paiement);
}
