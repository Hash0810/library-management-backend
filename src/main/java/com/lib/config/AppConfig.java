package com.lib.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Primary;


@Configuration
public class AppConfig {
    
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
