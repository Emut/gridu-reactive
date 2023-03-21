package com.example.reactordemo.services;

import com.example.reactordemo.domain.OrderServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class OrderServiceTest {

    OrderService orderService;

    ObjectMapper objectMapper = new ObjectMapper();

    private final static String URL_FORMAT = "/orderInfoService/order/phone?phoneNumber=%s";
    private final static String BASE_URL_FORMAT = "http://localhost:%d/orderInfoService";

    private final static String PHONE_NUMBER = "PHONE_NUMBER";
    private final static String PRODUCT_CODE_1 = "PC1";
    private final static String PRODUCT_CODE_2 = "PC2";

    private void initializeOrderServiceWithWebClient(int port) {
        orderService = new OrderService(WebClient.builder()
                .baseUrl(String.format(BASE_URL_FORMAT, port)).build());
    }

    @Test
    void getOrdersByPhoneNumber_whenRequestIsSuccessful_shouldReturnResults(WireMockRuntimeInfo wmRuntimeInfo) throws JsonProcessingException {
        initializeOrderServiceWithWebClient(wmRuntimeInfo.getHttpPort());

        List<OrderServiceResponse> results = List.of(
                OrderServiceResponse.builder().phoneNumber(PHONE_NUMBER).productCode(PRODUCT_CODE_1).build(),
                OrderServiceResponse.builder().phoneNumber(PHONE_NUMBER).productCode(PRODUCT_CODE_2).build()
        );

        stubFor(WireMock.get(String.format(URL_FORMAT, PHONE_NUMBER))
                .willReturn(ok(objectMapper.writeValueAsString(results))
                        .withHeader("Content-Type", MediaType.APPLICATION_NDJSON_VALUE)));

        Flux<OrderServiceResponse> result = orderService.getOrdersByPhoneNumber(PHONE_NUMBER);

        StepVerifier.create(result)
                .assertNext(orderServiceResponse -> {
                    assertThat(orderServiceResponse.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
                    assertThat(orderServiceResponse.getProductCode()).isEqualTo(PRODUCT_CODE_1);
                })
                .assertNext(orderServiceResponse -> {
                    assertThat(orderServiceResponse.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
                    assertThat(orderServiceResponse.getProductCode()).isEqualTo(PRODUCT_CODE_2);
                })
                .verifyComplete();
    }

    @Test
    void getOrdersByPhoneNumber_whenRequestIsNotSuccessful_shouldThrow(WireMockRuntimeInfo wmRuntimeInfo) {
        initializeOrderServiceWithWebClient(wmRuntimeInfo.getHttpPort());

        stubFor(WireMock.get(String.format(URL_FORMAT, PHONE_NUMBER))
                .willReturn(aResponse().withStatus(400)));

        Flux<OrderServiceResponse> result = orderService.getOrdersByPhoneNumber(PHONE_NUMBER);

        StepVerifier.create(result).expectError(Exception.class).verify();
    }
}