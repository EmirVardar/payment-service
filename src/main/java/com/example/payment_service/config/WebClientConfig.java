package com.example.payment_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient sipayClient() {
        return WebClient.builder()
                .baseUrl("https://provisioning.sipay.com.tr/ccpayment")
                .build();
    }
}
