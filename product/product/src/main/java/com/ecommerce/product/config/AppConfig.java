package com.ecommerce.product.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper(); // create instance first
        modelMapper.getConfiguration().setSkipNullEnabled(true); // then configure
        return modelMapper;
    }
}

