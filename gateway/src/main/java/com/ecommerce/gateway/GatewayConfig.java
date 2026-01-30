package com.ecommerce.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    // Redis Rate Limiter Bean
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // 10 requests per second, 20 burst capacity, 1 second replenish rate
        return new RedisRateLimiter(1, 10, 1);
    }

    // KeyResolver that uses IP address (avoids DNS issues)
    @Bean
    public KeyResolver hostNameKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress() != null
                        ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                        : "anonymous"
        );
    }

    // Route configuration
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // Product Service
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f
                                .retry(retry -> retry
                                        .setRetries(3) // retry 3 times for GET requests
                                        .setMethods(HttpMethod.GET))
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNameKeyResolver()))
                                .circuitBreaker(c -> c
                                        .setName("ecomBreaker")
                                        .setFallbackUri("forward:/fallback/products"))
                        )
                        .uri("lb://product-service"))

                // User Service
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNameKeyResolver()))
                                .circuitBreaker(c -> c
                                        .setName("userBreaker")
                                        .setFallbackUri("forward:/fallback/users"))
                        )
                        .uri("lb://user-service"))

                // Order Service
                .route("order-service", r -> r
                        .path("/api/orders/**", "/api/cart/**")
                        .filters(f -> f
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNameKeyResolver()))
                                .circuitBreaker(c -> c
                                        .setName("orderBreaker")
                                        .setFallbackUri("forward:/fallback/orders"))
                        )
                        .uri("lb://order-service"))

                // Eureka UI Proxy
                .route("eureka-ui", r -> r
                        .path("/eureka/**")
                        .uri("http://localhost:8761"))

                .build();
    }
}
