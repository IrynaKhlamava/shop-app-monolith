package com.example.lamashop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileDto extends UserProfileDto {

    private List<OrderDto> orders;

    private String shippingAddress;

    public CustomerProfileDto(String userId, String firstName, String lastName, String email, String shippingAddress, List<OrderDto> orders) {
        super(userId, firstName, lastName, email, "CUSTOMER");
        this.shippingAddress = shippingAddress;
        this.orders = orders;
    }
}
