package com.smartshop.smartshop.mapper;

import com.smartshop.smartshop.model.dto.response.OrderItemResponse;
import com.smartshop.smartshop.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "produit.id", target = "produitId")
    @Mapping(source = "produit.nom", target = "produitNom")
    OrderItemResponse toResponse(OrderItem orderItem);
}
