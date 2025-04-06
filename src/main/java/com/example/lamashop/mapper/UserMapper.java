package com.example.lamashop.mapper;

import com.example.lamashop.dto.CustomerProfileDto;
import com.example.lamashop.dto.OrderDto;
import com.example.lamashop.dto.UserProfileDto;
import com.example.lamashop.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "userId")
    UserProfileDto toUserProfileDto(User user);
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orders", target = "orders")
    CustomerProfileDto toCustomerProfileDto(User user, List<OrderDto> orders);

}
