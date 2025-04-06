package com.example.lamashop.mapper;

import com.example.lamashop.dto.CartItemDto;
import com.example.lamashop.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "productDto.id", target = "productId")
    @Mapping(source = "productDto.name", target = "productName")
    @Mapping(source = "productDto.price", target = "price")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(target = "returned", constant = "false")
    CartItemDto toCartItemDto(ProductDto productDto, int quantity);
}