package com.example.reactordemo.services;

import com.example.reactordemo.dto.ProductServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final WebClient productWebClient;

    public Flux<ProductServiceResponse> getProductsByProductCode(String productCode) {

        return productWebClient.get()
                .uri("/product/names?productCode={}", productCode).retrieve().bodyToFlux(ProductServiceResponse.class);
    }
}
