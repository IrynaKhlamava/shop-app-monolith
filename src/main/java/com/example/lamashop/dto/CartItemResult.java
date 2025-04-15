package com.example.lamashop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CartItemResult {

    private List<CartItemDto> availableItems;

    private List<MissingProductDto> missingItems;

}
