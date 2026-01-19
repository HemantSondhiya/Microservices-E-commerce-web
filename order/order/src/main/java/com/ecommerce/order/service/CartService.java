package com.ecommerce.order.service;

import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.model.CartItems;

import java.util.List;

public interface CartService {

    boolean addtoCart(String userID, CartItemRequest cartItemRequest);

    boolean deleteItemsFromCart(String userId, Long productId);


    List<CartItems> getAllCart(String userId);

    void clear(String userId);
}
