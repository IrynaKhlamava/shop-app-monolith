package com.example.lamashop.service;

import com.example.lamashop.dto.CartDto;
import com.example.lamashop.dto.CartItemDto;
import com.example.lamashop.dto.OrderDto;
import com.example.lamashop.exception.ResourceNotFoundException;
import com.example.lamashop.mapper.OrderMapper;
import com.example.lamashop.model.Order;
import com.example.lamashop.model.OrderItem;
import com.example.lamashop.model.enumType.OrderStatus;
import com.example.lamashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final ShoppingCartService shoppingCartService;

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final ProductService productService;

    private final OrderNumberGeneratorService orderNumberGeneratorService;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public OrderDto placeOrder(String userId, String providedAddress) {
        CartDto cart = shoppingCartService.getCart(userId);
        logger.info("Placing order for user {}", userId);

        long orderNumber = orderNumberGeneratorService.getNextOrderNumber();

        productService.updateStockAfterOrder(cart.getItems());

        Order order = saveOrder(userId, providedAddress, cart.getItems(), cart.getTotalPrice(), orderNumber);

        shoppingCartService.clearCart(userId);
        logger.info("Cart cleared for user {}", userId);

        return orderMapper.toDto(order);
    }

    private Order saveOrder(String userId, String shippingAddress, List<CartItemDto> items, BigDecimal totalPrice, long orderNumber) {
        List<OrderItem> orderItems = orderMapper.toOrderItems(items);

        Order order = Order.builder()
                .userId(userId)
                .items(orderItems)
                .totalPrice(totalPrice)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .shippingAddress(shippingAddress)
                .orderNumber(orderNumber)
                .build();

        order.setOrderNumber(orderNumber);

        return orderRepository.save(order);
    }

    public List<OrderDto> findOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    public List<OrderDto> getOrderHistory(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(orderMapper::toDto)
                .toList();
    }

    public OrderDto returnItem(String orderId, String productId) {
        logger.info("Processing return for order {}, product {}", orderId, productId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order"));

        for (OrderItem item : order.getItems()) {
            if (item.getProductId().equals(productId)) {
                item.setReturned(true);
                logger.info("User [{}] returned product [{}] from order [{}], quantity [{}]",
                        order.getUserId(), productId, orderId, item.getQuantity());

                productService.increaseStock(productId, item.getQuantity());
                break;
            }
        }

        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

}
