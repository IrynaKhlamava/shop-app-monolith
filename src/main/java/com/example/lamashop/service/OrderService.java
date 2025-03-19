package com.example.lamashop.service;

import com.example.lamashop.dto.CartDto;
import com.example.lamashop.dto.CartItemDto;
import com.example.lamashop.dto.OrderDto;
import com.example.lamashop.mapper.OrderMapper;
import com.example.lamashop.model.Order;
import com.example.lamashop.model.OrderItem;
import com.example.lamashop.model.enumType.OrderStatus;
import com.example.lamashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final ShoppingCartService shoppingCartService;

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    public OrderDto placeOrder(String userId, String shippingAddress) {
        CartDto cart = shoppingCartService.getCart(userId);

        Order order = saveOrder(userId, shippingAddress, cart.getItems(), cart.getTotalPrice());

        shoppingCartService.clearCart(userId);

        return orderMapper.toDto(order);
    }

    private Order saveOrder(String userId, String shippingAddress, List<CartItemDto> items, BigDecimal totalPrice) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemDto cartItemDto : items) {
            orderItems.add(orderMapper.toOrderItem(cartItemDto));
        }

        Order order = new Order(
                null,
                userId,
                orderItems,
                totalPrice,
                OrderStatus.CREATED,
                LocalDateTime.now(),
                shippingAddress
        );

        return orderRepository.save(order);
    }
}
