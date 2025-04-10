package com.example.lamashop.controller;

import com.example.lamashop.dto.OrderDto;
import com.example.lamashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}/place")
    public ResponseEntity<OrderDto> placeOrder(
            @PathVariable String userId,
            @RequestParam String shippingAddress) {
        return ResponseEntity.ok(orderService.placeOrder(userId, shippingAddress));
    }

    @PreAuthorize("#userId == principal or hasRole('ADMIN')")
    @GetMapping("/{userId}/history")
    public ResponseEntity<List<OrderDto>> getOrderHistory(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    @PostMapping("/{orderId}/return")
    public ResponseEntity<OrderDto> returnItem(
            @PathVariable String orderId,
            @RequestParam String productId
    ) {
        return ResponseEntity.ok(orderService.returnItem(orderId, productId));
    }

}
