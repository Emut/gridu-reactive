package com.example.reactordemo.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class OrderInfo {
    String orderNumber;
    String userName;
    String phoneNumber;
    String productCode;
    String productName;
    String productId;
}
