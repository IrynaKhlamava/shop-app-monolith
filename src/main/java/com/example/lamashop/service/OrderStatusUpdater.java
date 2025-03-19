package com.example.lamashop.service;

import com.example.lamashop.model.Order;
import com.example.lamashop.model.enumType.OrderStatus;
import com.example.lamashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatusUpdater {
    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 60000)
    public void updateOrderStatuses() {
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            switch (order.getStatus()) {
                case CREATED -> order.setStatus(OrderStatus.PROCESSING);
                case PROCESSING -> order.setStatus(OrderStatus.SHIPPING);
                case SHIPPING -> order.setStatus(OrderStatus.DELIVERED);
                case DELIVERED, ANNULLED, REFUNDED -> {
                    continue;
                }
            }
            orderRepository.save(order);
            System.out.println("Order ID: " + order.getId() + " updated to: " + order.getStatus());
        }
    }
}
