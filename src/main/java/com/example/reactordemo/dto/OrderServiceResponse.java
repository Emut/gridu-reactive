package com.example.reactordemo.dto;


import lombok.Data;

@Data
public class OrderServiceResponse {
    private String phoneNumber;
    private String orderNumber;
    private String productCode;
}
