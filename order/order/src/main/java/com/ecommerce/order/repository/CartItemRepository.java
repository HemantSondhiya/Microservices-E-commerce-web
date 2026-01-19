package com.ecommerce.order.repository;

import com.ecommerce.order.model.CartItems;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CartItemRepository extends JpaRepository<CartItems, Long> {

    CartItems findByUserIdAndProductId(String userId, Long productId);

    void deleteByUserIdAndProductId(String userId, Long productId);

    List<CartItems> findByUserId(String userId);

    void deleteByUserId(String userId);
}
