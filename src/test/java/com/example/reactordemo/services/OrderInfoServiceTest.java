package com.example.reactordemo.services;

import com.example.reactordemo.domain.OrderInfo;
import com.example.reactordemo.domain.OrderServiceResponse;
import com.example.reactordemo.domain.ProductServiceResponse;
import com.example.reactordemo.domain.User;
import com.example.reactordemo.repository.UserRepository;
import com.example.reactordemo.util.ReactiveUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderInfoServiceTest {

    @Mock
    OrderService orderService;

    @Mock
    ProductService productService;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    @Spy
    OrderInfoService orderInfoService;

    private static final String PRODUCT_CODE = "PC1";

    private static final String ANOTHER_PRODUCT_CODE = "PC2";

    private static final String PHONE_NUMBER = "PN";

    private static final String ORDER_NUMBER = "ON";

    private static final String PRODUCT_NAME = "PRODUCT_NAME1";

    private static final String ANOTHER_PRODUCT_NAME = "PRODUCT_NAME2";

    private static final String USER_ID = "USER_ID";

    private static final String USER_NAME = "USER_NAME";

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

    @Test
    void getUserOrdersByPhoneNumber() {
        OrderServiceResponse orderServiceResponse1 = OrderServiceResponse.builder()
                .phoneNumber(PHONE_NUMBER)
                .productCode(PRODUCT_CODE)
                .orderNumber(ORDER_NUMBER)
                .build();

        OrderServiceResponse orderServiceResponse2 = OrderServiceResponse.builder()
                .phoneNumber(PHONE_NUMBER)
                .productCode(ANOTHER_PRODUCT_CODE)
                .orderNumber(ORDER_NUMBER)
                .build();

        when(orderService.getOrdersByPhoneNumber(PHONE_NUMBER))
                .thenReturn(Flux.just(orderServiceResponse1, orderServiceResponse2));

        doReturn(Mono.just(ProductServiceResponse.builder()
                .productName(PRODUCT_NAME).build()))
                .when(orderInfoService).getProductWithHighestScore(PRODUCT_CODE);

        doReturn(Mono.just(ProductServiceResponse.builder()
                .productName(ANOTHER_PRODUCT_NAME).build()))
                .when(orderInfoService).getProductWithHighestScore(ANOTHER_PRODUCT_CODE);

        StepVerifier.create(orderInfoService.getUserOrdersByPhoneNumber(PHONE_NUMBER))
                .assertNext(orderInfo -> {
                    assertThat(orderInfo.getProductName()).isEqualTo(PRODUCT_NAME);
                    assertThat(orderInfo.getOrderNumber()).isEqualTo(ORDER_NUMBER);
                    assertThat(orderInfo.getProductCode()).isEqualTo(PRODUCT_CODE);
                })
                .assertNext(orderInfo -> {
                    assertThat(orderInfo.getProductName()).isEqualTo(ANOTHER_PRODUCT_NAME);
                    assertThat(orderInfo.getOrderNumber()).isEqualTo(ORDER_NUMBER);
                    assertThat(orderInfo.getProductCode()).isEqualTo(ANOTHER_PRODUCT_CODE);
                })
                .expectAccessibleContext().hasKey(ReactiveUtils.MDC_KEY).then()
                .verifyComplete();

    }

    @Test
    void getUserOrdersByUserId_whenUserNotFound_shouldThrow() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        StepVerifier.create(orderInfoService.getUserOrdersByUserId(USER_ID))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getUserOrdersByUserId_whenUserFound_shouldReturnOrderInfo() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Mono.just(User.builder()._id(USER_ID).name(USER_NAME).phone(PHONE_NUMBER).build()));
        doReturn(Flux.just(OrderInfo.builder().build()))
                .when(orderInfoService).getUserOrdersByPhoneNumber(PHONE_NUMBER);

        StepVerifier.create(orderInfoService.getUserOrdersByUserId(USER_ID))
                .assertNext(orderInfo -> assertThat(orderInfo.getUserName()).isEqualTo(USER_NAME))
                .verifyComplete();
    }
}