package com.example.lamashop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String id;

    private long orderNumber;

    private List<OrderItemDto> items;

    private BigDecimal totalPrice;

    private String status;

    private LocalDateTime createdAt;

    private String shippingAddress;
}
