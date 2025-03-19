package com.example.lamashop.model;

import com.example.lamashop.model.enumType.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String id;

    private String userId;

    private List<OrderItem> items;

    private BigDecimal totalPrice;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private String shippingAddress;

}
