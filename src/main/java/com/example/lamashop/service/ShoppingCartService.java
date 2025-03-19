package com.example.lamashop.service;

import com.example.lamashop.dto.CartDto;
import com.example.lamashop.dto.CartItemDto;
import com.example.lamashop.dto.ProductDto;
import com.example.lamashop.mapper.CartMapper;
import com.example.lamashop.model.Product;
import com.example.lamashop.exception.CartEmptyException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);

    private static final String CART_PREFIX = "cart:";

    private final RedisTemplate<String, Object> redisTemplate;

    private final ProductService productService;

    private final CartMapper cartMapper;

    public void addToCart(String userId, String productId, int quantity) {
        String cartKey = CART_PREFIX + userId;

        logger.info("User {} is adding product {} (qty: {}) to cart.", userId, productId, quantity);

        Product product = productService.validateProductAvailability(productId, quantity);

        Object existingQuantityObj = redisTemplate.opsForHash().get(cartKey, productId);
        int existingQuantity = (existingQuantityObj instanceof Integer) ? (Integer) existingQuantityObj : 0;

        int newQuantity = existingQuantity + quantity;

        redisTemplate.opsForHash().put(cartKey, productId, newQuantity);

        logger.info("User {} updated cart: product '{}' now has quantity {}.", userId, product.getName(), newQuantity);
    }

    public CartDto getCart(String userId) {
        String cartKey = CART_PREFIX + userId;
        Map<Object, Object> cartItems = redisTemplate.opsForHash().entries(cartKey);

        if (cartItems.isEmpty()) {
            throw new CartEmptyException();
        }

        List<CartItemDto> items = convertCartItems(cartItems);
        BigDecimal totalPrice = calculateTotalPrice(items);

        return new CartDto(userId, items, totalPrice);
    }

    private List<CartItemDto> convertCartItems(Map<Object, Object> cartItems) {
        List<CartItemDto> items = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : cartItems.entrySet()) {
            String productId = entry.getKey().toString();
            int quantity = (int) entry.getValue();

            ProductDto productDto = productService.getProductById(productId);
            CartItemDto cartItem = cartMapper.toCartItemDto(productDto);
            cartItem.setQuantity(quantity);

            items.add(cartItem);
        }

        return items;
    }

    private BigDecimal calculateTotalPrice(List<CartItemDto> items) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItemDto item : items) {
            totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        return totalPrice;
    }

    public void removeFromCart(String userId, String productId) {
        redisTemplate.opsForHash().delete(CART_PREFIX + userId, productId);
    }

    public void clearCart(String userId) {
        redisTemplate.delete(CART_PREFIX + userId);
    }
}
