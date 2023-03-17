package com.example.reactordemo.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderServiceResponse {
    private String phoneNumber;
    private String orderNumber;
    private String productCode;
}
