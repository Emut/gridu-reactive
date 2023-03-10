package com.example.reactordemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {


    @Value("${order-service.baseUrl}")
    private String orderServiceUrl;

    @Value("${product-service.baseUrl}")
    private String productServiceUrl;

    @Bean(name = "orderWebClient")
    WebClient getOrderServiceWebClient(){
        return WebClient.builder().baseUrl(orderServiceUrl).build();
    }

    @Bean(name = "productWebClient")
    WebClient getProductServiceWebClient(){
        return WebClient.builder().baseUrl(productServiceUrl).build();
    }
}
