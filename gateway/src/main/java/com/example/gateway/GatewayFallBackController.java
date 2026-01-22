package com.example.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
public class GatewayFallBackController {

    @GetMapping("/fallback/products")
    public Mono<ResponseEntity<List<String>>> productFallback() {
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Collections.singletonList(
                                "Product Service is unavailable! Please try later"
                        ))
        );
    }
}
