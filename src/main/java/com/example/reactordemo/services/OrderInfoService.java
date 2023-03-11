package com.example.reactordemo.services;

import com.example.reactordemo.domain.OrderInfo;
import com.example.reactordemo.domain.OrderServiceResponse;
import com.example.reactordemo.domain.ProductServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderInfoService {

    private final OrderService orderService;
    private final ProductService productService;

    public Flux<OrderInfo> getUserOrdersByPhoneNumber(String phoneNumber) {
        Flux<OrderServiceResponse> ordersFlux = orderService.getOrdersByPhoneNumber(phoneNumber);

        return ordersFlux.flatMap(orderServiceResponse -> {
            OrderInfo.OrderInfoBuilder orderInfoBuilder = OrderInfo.builder();
            orderInfoBuilder.phoneNumber(phoneNumber)
                    .orderNumber(orderServiceResponse.getOrderNumber())
                    .productCode(orderServiceResponse.getProductCode());
            Mono<ProductServiceResponse> productWithHighestScore = getProductWithHighestScore(orderServiceResponse.getProductCode());
            return productWithHighestScore
                    .map(product -> orderInfoBuilder.productName(product.getProductName())
                            .productId(product.getProductId()).build());
        });
    }

    @VisibleForTesting
    Mono<ProductServiceResponse> getProductWithHighestScore(String productCode) {
        Flux<ProductServiceResponse> productsFlux = productService.getProductsByProductCode(productCode)
                .onErrorComplete();
        return productsFlux.collectList().mapNotNull(
                listOfProducts -> listOfProducts.stream()
                        .max(Comparator.comparingDouble(ProductServiceResponse::getScore))
                        .orElse(new ProductServiceResponse())
        );
    }
}
