package com.example.reactordemo.controller;

import com.example.reactordemo.domain.OrderInfo;
import com.example.reactordemo.services.OrderInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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

    @GetMapping("orderInfo/getByUserId")
    public Flux<OrderInfo> getOrderInfoByUserId(@RequestParam String userId,
                                                @RequestHeader String requestId) {
        MDC.put("requestId", requestId);
        log.info("start getOrderInfoByPhoneNumber with reqId:{} userId:{}", requestId, userId);
        var result = orderInfoService.getUserOrdersByUserId(userId);
        log.info("end getOrderInfoByPhoneNumber with userId:{}", userId);
        return result;
    }
}
