package com.example.lamashop.mapper;

import com.example.lamashop.dto.CartItemDto;
import com.example.lamashop.dto.ProductDto;
import com.example.lamashop.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "id", target = "productId")
    @Mapping(source = "name", target = "productName")
    @Mapping(source = "price", target = "price")
    CartItemDto toCartItemDto(ProductDto productDto);
}