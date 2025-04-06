package com.example.lamashop.service;

import com.example.lamashop.dto.CustomerProfileDto;
import com.example.lamashop.dto.OrderDto;
import com.example.lamashop.mapper.UserMapper;
import com.example.lamashop.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerProfileService {

    private final OrderService orderService;

    private final UserMapper userMapper;

    public CustomerProfileDto getCustomerProfile(User user) {
        List<OrderDto> orderDtos = orderService.findOrdersByUserId(user.getId());
        return userMapper.toCustomerProfileDto(user, orderDtos);
    }
}
