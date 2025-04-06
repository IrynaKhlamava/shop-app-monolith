package com.example.lamashop.controller;

import com.example.lamashop.dto.CustomerProfileDto;
import com.example.lamashop.dto.UserProfileDto;
import com.example.lamashop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}/profile")
    @PreAuthorize("#userId == authentication.principal or hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PutMapping("/{userId}/shipping-address")
    public ResponseEntity<CustomerProfileDto> updateShippingAddress(@PathVariable String userId, @RequestParam String newAddress) {
        return ResponseEntity.ok(userService.updateShippingAddress(userId, newAddress));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserProfileDto> getUsers(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        return userService.getPagedUsers(query, page, size);
    }

}
