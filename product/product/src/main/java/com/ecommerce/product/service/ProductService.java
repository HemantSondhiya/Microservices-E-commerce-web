package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductService {
     ProductResponse createProduct(ProductRequest productRequest);


    Optional<ProductResponse> updateProduct(ProductRequest productRequest, Long id);


    boolean deleteProduct(Long id);


    List<ProductResponse> searchProducts(String keyword);


    List<ProductResponse> getAllProducts();

    Optional<ProductResponse> getProductById(Long id);
}
