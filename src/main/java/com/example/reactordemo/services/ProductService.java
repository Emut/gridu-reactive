package com.example.reactordemo.services;

import com.example.reactordemo.domain.ProductServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final WebClient productWebClient;

    private static final int TIMEOUT_IN_SECONDS = 5;

    public Flux<ProductServiceResponse> getProductsByProductCode(String productCode) {

        return productWebClient.get()
                .uri("/product/names?productCode={0}", productCode)
                .retrieve().bodyToFlux(ProductServiceResponse.class)
                .timeout(Duration.ofSeconds(TIMEOUT_IN_SECONDS))
                .doOnNext(productServiceResponse -> log.info("Received:\"{}\" for request for productCode:\"{}\"",
                        productServiceResponse, productCode))
                .doOnError(throwable -> log.error("Request for productCode:\"{}\" failed,",
                        productCode, throwable));
    }
}
