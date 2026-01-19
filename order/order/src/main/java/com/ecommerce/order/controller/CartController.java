package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.model.CartItems;
import com.ecommerce.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @PostMapping
    public ResponseEntity<String> addtoCart(@RequestHeader("X-User-ID") String userID,
                                            @RequestBody CartItemRequest cartItemRequest){
          if (!cartService.addtoCart(userID,cartItemRequest)){
              return ResponseEntity.badRequest().body("Product out of stock and User not found And product not Found");
          }
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void>deleteCart(@RequestHeader("X-User-ID") String userID,
                                            @PathVariable Long productId){
        boolean deleted = cartService.deleteItemsFromCart(userID,productId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<CartItems>>deleteCart(@RequestHeader("X-User-ID") String userID){
        return ResponseEntity.ok(cartService.getAllCart(userID));
    }
}
