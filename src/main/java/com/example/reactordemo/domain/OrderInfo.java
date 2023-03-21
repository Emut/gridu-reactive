package com.example.reactordemo.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@Accessors(chain = true)
@ToString
public class OrderInfo {
    String orderNumber;
    String userName;
    String phoneNumber;
    String productCode;
    String productName;
    String productId;
}
