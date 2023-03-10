package com.example.reactordemo.dto;

import lombok.Data;

@Data
public class ProductServiceResponse {
    private String productId;
    private String productCode;
    private String productName;
    private double score;
}
