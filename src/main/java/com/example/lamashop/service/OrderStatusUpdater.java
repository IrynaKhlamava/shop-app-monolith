package com.example.lamashop.service;

import com.example.lamashop.model.Order;
import com.example.lamashop.model.enumType.OrderStatus;
import com.example.lamashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class OrderStatusUpdater {
    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 60000)
    public void updateOrderStatuses() {
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.CREATED,
                OrderStatus.PROCESSING,
                OrderStatus.SHIPPING
        );

        try (Stream<Order> orders = orderRepository.streamByStatusIn(activeStatuses)) {
            orders.forEach(order -> {
                switch (order.getStatus()) {
                    case CREATED -> order.setStatus(OrderStatus.PROCESSING);
                    case PROCESSING -> order.setStatus(OrderStatus.SHIPPING);
                    case SHIPPING -> order.setStatus(OrderStatus.DELIVERED);
                    default -> {
                        return;
                    }
                }

                orderRepository.save(order);
                System.out.println("Order ID: " + order.getId() + " updated to: " + order.getStatus());
            });
        }
    }
}
