package com.example.reactordemo.services;

import com.example.reactordemo.dto.OrderServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final WebClient orderWebClient;

    public Flux<OrderServiceResponse> getOrdersByPhoneNumber(String phoneNumber) {

        return orderWebClient.get()
                .uri("/order/phone?phoneNumber={}", phoneNumber).retrieve()
                .bodyToFlux(OrderServiceResponse.class);
    }
}
