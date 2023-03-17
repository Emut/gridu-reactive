package com.example.reactordemo.services;

import com.example.reactordemo.domain.OrderServiceResponse;
import com.example.reactordemo.util.ReactiveUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final WebClient orderWebClient;

    public Flux<OrderServiceResponse> getOrdersByPhoneNumber(String phoneNumber) {

        return orderWebClient.get()
                .uri("/order/phone?phoneNumber={0}", phoneNumber).retrieve()
                .bodyToFlux(OrderServiceResponse.class)
                .doOnEach(signal -> ReactiveUtils.logOnEachWithMdcFromContext(signal,
                        orderServiceResponse -> log.info("Received:\"{}\" for request for phoneNumber:\"{}\"",
                                orderServiceResponse, phoneNumber),
                        throwable -> log.error("Request for phoneNumber:\"{}\" failed,",
                                phoneNumber, throwable)))
                .contextWrite(Context.of("CARAN", MDC.getCopyOfContextMap()));
    }
}
