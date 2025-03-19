package com.example.lamashop.mapper;

import com.example.lamashop.dto.CartItemDto;
import com.example.lamashop.dto.OrderDto;
import com.example.lamashop.model.Order;
import com.example.lamashop.model.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto toDto(Order order);

    OrderItem toOrderItem(CartItemDto cartItemDto);

}
