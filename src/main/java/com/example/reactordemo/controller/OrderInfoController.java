package com.example.reactordemo.controller;

import com.example.reactordemo.domain.OrderInfo;
import com.example.reactordemo.services.OrderInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderInfoController {

    private final OrderInfoService orderInfoService;

    @GetMapping("orderInfo/getByPhoneNumber")
    public Flux<OrderInfo> getOrderInfoByPhoneNumber(@RequestParam String phoneNumber,
                                                     @RequestHeader String requestId) {
        log.info("start getOrderInfoByPhoneNumber with reqId:{} phoneNumber:{}",
                requestId, phoneNumber);
        var result = orderInfoService.getUserOrdersByPhoneNumber(phoneNumber);
        log.info("end getOrderInfoByPhoneNumber with phoneNumber:{}", phoneNumber);
        return result;
    }
}