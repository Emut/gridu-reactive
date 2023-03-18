package com.example.reactordemo.services;

import com.example.reactordemo.domain.OrderInfo;
import com.example.reactordemo.domain.OrderServiceResponse;
import com.example.reactordemo.domain.ProductServiceResponse;
import com.example.reactordemo.repository.UserRepository;
import com.example.reactordemo.util.ReactiveUtils;
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
    private final UserRepository userRepository;

    public Flux<OrderInfo> getUserOrdersByUserId(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Can not find user with id:" + userId)))
                .doOnEach(signal -> ReactiveUtils.logOnEachWithMdcFromContext(signal,
                        user -> log.info("User findById:{} returned:{}", userId, user),
                        throwable -> log.error("User findById:{} error:", userId, throwable)))
                .flatMapMany(user -> getUserOrdersByPhoneNumber(user.getPhone())
                        .map(orderInfo -> orderInfo.setUserName(user.getName())))
                .contextWrite(ReactiveUtils::mdcContextModifier);
    }

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
        }).contextWrite(ReactiveUtils::mdcContextModifier);
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
