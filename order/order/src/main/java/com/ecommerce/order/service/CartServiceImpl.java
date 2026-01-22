package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.client.UserServiceClient;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.model.CartItems;

import com.ecommerce.order.repository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    int Attempt = 0;


    @Override
    @Transactional
   // @CircuitBreaker(name = "productService", fallbackMethod = "addToCartFallBack")
    @Retry(name = "retryBreaker", fallbackMethod = "addToCartFallBack")
    public boolean addtoCart(String userId, CartItemRequest cartItemRequest) {
        System.out.println("Retry Attempt"+ ++Attempt);

        // 1️⃣ Get product details
        ProductResponse productResponse =
                productServiceClient.getProductDetails(String.valueOf(cartItemRequest.getProductId()));
        if (productResponse == null) return false;

        // 2️⃣ Check stock quantity
        if (productResponse.getStockQuantity() < cartItemRequest.getQuantity()) return false;

        // 3️⃣ Get user details
        UserResponse userResponse = userServiceClient.getUserDetails(userId);
        if (userResponse == null) return false;

        // 4️⃣ Check if product is already in cart
        Long productId = cartItemRequest.getProductId();
        CartItems existingCartItem =
                cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(
                    existingCartItem.getQuantity() + cartItemRequest.getQuantity()
            );
            existingCartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(existingCartItem);
        } else {
            CartItems cartItem = new CartItems();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(cartItemRequest.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(1000.00));

            cartItemRepository.save(cartItem);
        }

        return true;
    }

    public  boolean addToCartFallBack(String userId, CartItemRequest cartItemRequest,
                                       Exception exception ){
        exception.printStackTrace();
        return false;
    }

    @Override
    @Transactional
    public boolean deleteItemsFromCart(String userId, Long productId) {
        CartItems item = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (item != null) {
            cartItemRepository.delete(item);
            return true;
        }

        return false;
    }

    @Override
    public List<CartItems> getAllCart(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void clear(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}

