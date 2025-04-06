package com.example.lamashop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private String productId;

    private String productName;

    private int quantity;

    private BigDecimal price;

    private boolean returned;
}

