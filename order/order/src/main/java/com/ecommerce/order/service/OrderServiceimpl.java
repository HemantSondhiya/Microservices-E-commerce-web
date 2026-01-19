package com.ecommerce.order.service;


import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItems;
import com.ecommerce.order.model.CartItems;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.*;
import com.ecommerce.order.repository.OrderRepository;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceimpl implements OrderService {
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public Optional<OrderResponse> createOrder(String userId) {
        // validate the cart items
        List<CartItems> cartItems = cartService.getAllCart(String.valueOf(userId));
        if (cartItems.isEmpty()) {
            return Optional.empty();
        }
//
//        // validate the user
//        Optional<User> userOptional = userRepository.findById(Long.valueOf(userID));
//        if (userOptional.isEmpty()) {
//            return Optional.empty();
//        }
//        User user = userOptional.get();

        // create the order
        Order order = new Order();
        order.setUserId((userId));
        order.setStatus(OrderStatus.CONFIRM);

        // map cart items to order items with totals
        List<OrderItems> orderItems = cartItems.stream()
                .map(item -> {
                    BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new OrderItems(
                            null,
                            item.getProductId(),
                            item.getQuantity(),
                            item.getPrice(),
                            itemTotal,
                            order
                    );
                })
                .toList();

        order.setItems(orderItems);

        // calculate total order amount
        BigDecimal totalPrice = orderItems.stream()
                .map(OrderItems::getItemTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalPrice);

        // save the order
        Order savedOrder = orderRepository.save(order);

        // clear the cart
        cartService.clear(String.valueOf(userId));

        // map to DTO
        return Optional.of(modelMapper.map(savedOrder, OrderResponse.class));
    }
}