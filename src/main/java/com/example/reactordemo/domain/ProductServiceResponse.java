package com.example.reactordemo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductServiceResponse {
    private String productId;
    private String productCode;
    private String productName;
    private double score;
}
