package com.example.lamashop.service;

import com.example.lamashop.dto.CartDto;
import com.example.lamashop.dto.CartItemDto;
import com.example.lamashop.dto.ProductDto;
import com.example.lamashop.mapper.CartMapper;
import com.example.lamashop.model.Product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ShoppingCartService {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);

    private static final String CART_PREFIX = "cart:";

    private final ProductService productService;

    private final CartMapper cartMapper;

    private final RedisTemplate<String, String> cartRedisTemplate;

    public ShoppingCartService(ProductService productService, CartMapper cartMapper, @Qualifier("cartRedisTemplate") RedisTemplate<String, String> cartRedisTemplate) {
        this.productService = productService;
        this.cartMapper = cartMapper;
        this.cartRedisTemplate = cartRedisTemplate;
    }

    public CartDto addToCart(String userId, String productId, int quantity) {
        String cartKey = CART_PREFIX + userId;

        logger.info("User {} is adding product {} (qty: {}) to cart.", userId, productId, quantity);

        Product product = productService.validateProductAvailability(productId, quantity);

        String existingQuantityStr = cartRedisTemplate.<String, String>opsForHash().get(cartKey, productId);
        int existingQuantity = Optional.ofNullable(existingQuantityStr)
                .map(Integer::parseInt)
                .orElse(0);

        int newQuantity = existingQuantity + quantity;

        cartRedisTemplate.opsForHash().put(cartKey, productId, String.valueOf(newQuantity));

        logger.info("User {} updated cart: product '{}' now has quantity {}.", userId, product.getName(), newQuantity);

        return getCart(userId);
    }

    public CartDto getCart(String userId) {
        String cartKey = CART_PREFIX + userId;
        Map<String, String> cartItems = cartRedisTemplate.<String, String>opsForHash().entries(cartKey);

        if (cartItems.isEmpty()) {
            return new CartDto(userId, new ArrayList<>(), 0, BigDecimal.ZERO);
        }

        List<CartItemDto> items = convertCartItems(cartItems);

        int totalQuantity = calculateTotalQuantity(items);

        BigDecimal totalPrice = calculateTotalPrice(items);

        return new CartDto(userId, items, totalQuantity, totalPrice);
    }

    private int calculateTotalQuantity(List<CartItemDto> items) {
        return items.stream()
                .mapToInt(CartItemDto::getQuantity)
                .sum();
    }

    private List<CartItemDto> convertCartItems(Map<String, String> cartItems) {
        List<CartItemDto> items = new ArrayList<>();

        for (Map.Entry<String, String> entry : cartItems.entrySet()) {
            String productId = entry.getKey();
            int quantity =Integer.parseInt(entry.getValue());

            ProductDto productDto = productService.getProductById(productId);
            CartItemDto cartItem = cartMapper.toCartItemDto(productDto, quantity);

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
        cartRedisTemplate.opsForHash().delete(CART_PREFIX + userId, productId);
    }

    public void clearCart(String userId) {
        cartRedisTemplate.delete(CART_PREFIX + userId);
    }

    public void updateCartItem(String userId, String productId, int quantity) {
        String cartKey = CART_PREFIX + userId;

        if (quantity <= 0) {
            cartRedisTemplate.opsForHash().delete(cartKey, productId);
            return;
        }

        productService.validateProductAvailability(productId, quantity);
        cartRedisTemplate.opsForHash().put(cartKey, productId, String.valueOf(quantity));
    }

    public CartDto updateCartAndReturn(String userId, String productId, int quantity) {
        updateCartItem(userId, productId, quantity);
        return getCart(userId);
    }

    public CartDto removeFromCartAndReturn(String userId, String productId) {
        removeFromCart(userId, productId);
        return getCart(userId);
    }

}
