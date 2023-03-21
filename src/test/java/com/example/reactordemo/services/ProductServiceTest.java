package com.example.reactordemo.services;

import com.example.reactordemo.domain.ProductServiceResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class ProductServiceTest {

    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final static String URL_FORMAT = "/productInfoService/product/names?productCode=%s";
    private final static String BASE_URL_FORMAT = "http://localhost:%d/productInfoService";

    private final static int DEFAULT_TIMEOUT = 5000;

    private final static String PHONE_NUMBER = "PHONE_NUMBER";
    private final static String PRODUCT_CODE = "PC";
    private final static String PRODUCT_NAME_1 = "PRODUCT_NAME_1";
    private final static String PRODUCT_NAME_2 = "PRODUCT_NAME_2";

    private void initializeOrderServiceWithWebClient(int port) {
        initializeOrderServiceWithWebClient(port, DEFAULT_TIMEOUT);
    }

    private void initializeOrderServiceWithWebClient(int port, int timeout) {
        productService = new ProductService(WebClient.builder()
                .baseUrl(String.format(BASE_URL_FORMAT, port)).build(), timeout);
    }

    @Test
    void getOrdersByPhoneNumber_whenRequestIsSuccessful_shouldReturnResults(WireMockRuntimeInfo wmRuntimeInfo) throws JsonProcessingException {
        initializeOrderServiceWithWebClient(wmRuntimeInfo.getHttpPort());

        List<ProductServiceResponse> results = List.of(
                ProductServiceResponse.builder().productCode(PRODUCT_CODE).productName(PRODUCT_NAME_1).build(),
                ProductServiceResponse.builder().productCode(PRODUCT_CODE).productName(PRODUCT_NAME_2).build()
        );

        stubFor(WireMock.get(String.format(URL_FORMAT, PRODUCT_CODE))
                .willReturn(ok(objectMapper.writeValueAsString(results))
                        .withHeader("Content-Type", MediaType.APPLICATION_NDJSON_VALUE)));

        Flux<ProductServiceResponse> result = productService.getProductsByProductCode(PRODUCT_CODE);

        StepVerifier.create(result)
                .assertNext(productServiceResponse -> {
                    assertThat(productServiceResponse.getProductCode()).isEqualTo(PRODUCT_CODE);
                    assertThat(productServiceResponse.getProductName()).isEqualTo(PRODUCT_NAME_1);
                })
                .assertNext(productServiceResponse -> {
                    assertThat(productServiceResponse.getProductCode()).isEqualTo(PRODUCT_CODE);
                    assertThat(productServiceResponse.getProductName()).isEqualTo(PRODUCT_NAME_2);
                })
                .verifyComplete();
    }

    @Test
    void getOrdersByPhoneNumber_whenRequestIsNotSuccessful_shouldThrow(WireMockRuntimeInfo wmRuntimeInfo) {
        initializeOrderServiceWithWebClient(wmRuntimeInfo.getHttpPort());

        stubFor(WireMock.get(String.format(URL_FORMAT, PRODUCT_CODE))
                .willReturn(aResponse().withStatus(400)));

        Flux<ProductServiceResponse> result = productService.getProductsByProductCode(PRODUCT_CODE);

        StepVerifier.create(result).expectError(Exception.class).verify();
    }

    @Test
    void getOrdersByPhoneNumber_whenRequestIsTimedout_shouldReceiveTimeoutException(WireMockRuntimeInfo wmRuntimeInfo) throws JsonProcessingException {
        initializeOrderServiceWithWebClient(wmRuntimeInfo.getHttpPort(), 10);

        stubFor(WireMock.get(String.format(URL_FORMAT, PRODUCT_CODE))
                .willReturn(ok(objectMapper.writeValueAsString(Collections.emptyList()))
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withFixedDelay(1000)));

        Flux<ProductServiceResponse> result = productService.getProductsByProductCode(PRODUCT_CODE);

        StepVerifier.create(result).expectError(TimeoutException.class).verify();
    }
}