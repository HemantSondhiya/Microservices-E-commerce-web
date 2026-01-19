package com.ecommerce.order.client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Optional;

@Configuration
public class UserServiceClientConfig {

    @Bean
    public UserServiceClient userServiceClient(RestClient.Builder builder){
        RestClient restClient = builder
                .baseUrl("http://user-service")
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> Optional.empty())
                .build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(restClientAdapter)
                .build();

        return httpServiceProxyFactory.createClient(UserServiceClient.class);
    }

}
