package com.example.reactordemo.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderServiceResponse {
    private String phoneNumber;
    private String orderNumber;
    private String productCode;
}
