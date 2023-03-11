package com.example.reactordemo.services;

import com.example.reactordemo.domain.ProductServiceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderInfoServiceTest {

    @Mock
    ProductService productService;

    @InjectMocks
    OrderInfoService orderInfoService;

    private static final String PRODUCT_CODE = "PC";

    @Test
    void getProductWithHighestScore_whenSingleProduct_shouldReturnIt() {
        when(productService.getProductsByProductCode(PRODUCT_CODE))
                .thenReturn(Flux.just(ProductServiceResponse.builder().score(99.9).build()));

        Mono<ProductServiceResponse> result = orderInfoService.getProductWithHighestScore(PRODUCT_CODE);

        StepVerifier.create(result).assertNext(
                productServiceResponse -> assertThat(productServiceResponse.getScore()).isEqualTo(99.9)
        ).verifyComplete();
    }

    @Test
    void getProductWithHighestScore_whenNoProduct_shouldReturnEmptyProduct() {
        when(productService.getProductsByProductCode(PRODUCT_CODE))
                .thenReturn(Flux.empty());

        Mono<ProductServiceResponse> result = orderInfoService.getProductWithHighestScore(PRODUCT_CODE);

        StepVerifier.create(result).assertNext(
                productServiceResponse -> {
                    assertThat(productServiceResponse.getScore()).isZero();
                    assertThat(productServiceResponse.getProductId()).isNull();
                    assertThat(productServiceResponse.getProductName()).isNull();
                    assertThat(productServiceResponse.getProductCode()).isNull();
                }
        ).verifyComplete();
    }

    @Test
    void getProductWithHighestScore_whenMultipleProducts_shouldReturnMaxScore() {
        var expectedResult = ProductServiceResponse.builder().score(99).build();
        when(productService.getProductsByProductCode(PRODUCT_CODE))
                .thenReturn(Flux.just(ProductServiceResponse.builder().score(1).build(),
                        ProductServiceResponse.builder().score(2).build(),
                        expectedResult,
                        ProductServiceResponse.builder().score(3).build(),
                        ProductServiceResponse.builder().score(4).build(),
                        ProductServiceResponse.builder().score(5).build()));

        Mono<ProductServiceResponse> result = orderInfoService.getProductWithHighestScore(PRODUCT_CODE);

        StepVerifier.create(result).assertNext(
                productServiceResponse -> assertThat(productServiceResponse).isSameAs(expectedResult)
        ).verifyComplete();
    }

    @Test
    void getProductWithHighestScore_whenError_shouldReturnEmptyProduct() {
        when(productService.getProductsByProductCode(PRODUCT_CODE))
                .thenReturn(Flux.error(new RuntimeException("An error occurred during fetch")));

        Mono<ProductServiceResponse> result = orderInfoService.getProductWithHighestScore(PRODUCT_CODE);

        StepVerifier.create(result).assertNext(
                productServiceResponse -> {
                    assertThat(productServiceResponse.getScore()).isZero();
                    assertThat(productServiceResponse.getProductId()).isNull();
                    assertThat(productServiceResponse.getProductName()).isNull();
                    assertThat(productServiceResponse.getProductCode()).isNull();
                }
        ).verifyComplete();
    }

}