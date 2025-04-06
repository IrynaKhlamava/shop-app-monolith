package com.example.lamashop.mapper;

import com.example.lamashop.dto.ProductDto;
import com.example.lamashop.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDto dto);

    ProductDto toDto(Product entity);

}
