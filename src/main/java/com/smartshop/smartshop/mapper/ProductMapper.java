package com.smartshop.smartshop.mapper;

import com.smartshop.smartshop.model.dto.request.ProductRequest;
import com.smartshop.smartshop.model.dto.response.ProductResponse;
import com.smartshop.smartshop.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "orderItems", ignore = true)
    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    void updateEntity(ProductRequest request, @MappingTarget Product product);
}
