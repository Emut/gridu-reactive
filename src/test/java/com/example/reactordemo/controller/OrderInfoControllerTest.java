package com.example.reactordemo.controller;

import com.example.reactordemo.domain.OrderInfo;
import com.example.reactordemo.services.OrderInfoService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
@WebFluxTest(value = OrderInfoController.class)
class OrderInfoControllerTest {

    @MockBean
    private OrderInfoService orderInfoService;

    @Autowired
    private WebTestClient webTestClient;

    private static final String USER_ID = "USER_ID";
    private static final String USER_NAME = "USER_NAME";
    private static final String PRODUCT_NAME_1 = "PN1";
    private static final String PRODUCT_NAME_2 = "PN2";
    private static final String ERROR_MESSAGE = "Something went wrong";


    @Test
    void getOrderInfoByUserId() {
        OrderInfo orderInfo1 = OrderInfo.builder().userName(USER_NAME).productName(PRODUCT_NAME_1).build();
        OrderInfo orderInfo2 = OrderInfo.builder().userName(USER_NAME).productName(PRODUCT_NAME_2).build();
        Mockito.when(orderInfoService.getUserOrdersByUserId(USER_ID))
                .thenReturn(Flux.just(orderInfo1, orderInfo2));

        webTestClient.get()
                .uri("/orderInfo/getByUserId?userId={0}", USER_ID)
                .header("requestId", "aRequestId")
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.[0].userName").isEqualTo(USER_NAME)
                .jsonPath("$", Matchers.arrayContaining(orderInfo1, orderInfo2));
    }

    @Test
    void getOrderInfoByUserId_whenServiceThrowsIllegalArgument_returnErrorResponse() {
        Mockito.when(orderInfoService.getUserOrdersByUserId(USER_ID))
                .thenThrow(new IllegalArgumentException(ERROR_MESSAGE));

        webTestClient.get()
                .uri("/orderInfo/getByUserId?userId={0}", USER_ID)
                .header("requestId", "aRequestId")
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$").isEqualTo(ERROR_MESSAGE);
    }
}