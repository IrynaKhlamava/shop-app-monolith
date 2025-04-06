package com.example.lamashop.controller;

import com.example.lamashop.dto.CartDto;
import com.example.lamashop.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartDto> addToCart(
            @PathVariable String userId,
            @RequestParam String productId,
            @RequestParam int quantity) {

        return ResponseEntity.ok(shoppingCartService.addToCart(userId, productId, quantity));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable String userId) {

        return ResponseEntity.ok(shoppingCartService.getCart(userId));
    }

    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<CartDto> removeFromCart(
            @PathVariable String userId,
            @RequestParam String productId) {

        return ResponseEntity.ok(shoppingCartService.removeFromCartAndReturn(userId, productId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        shoppingCartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/update")
    public ResponseEntity<CartDto> updateCartItem(
            @PathVariable String userId,
            @RequestParam String productId,
            @RequestParam int quantity) {

        return ResponseEntity.ok(shoppingCartService.updateCartAndReturn(userId, productId, quantity));
    }

}