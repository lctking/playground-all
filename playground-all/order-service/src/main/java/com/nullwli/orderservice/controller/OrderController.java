package com.nullwli.orderservice.controller;

import com.nullwli.orderservice.dto.req.OrderCreateReqDTO;
import com.nullwli.orderservice.dto.req.OrderStatusUpdateReqDTO;
import com.nullwli.orderservice.dto.req.OrderUpdateReqDTO;
import com.nullwli.orderservice.dto.resp.OrderCreateRespDTO;
import com.nullwli.orderservice.mq.event.DelayCloseOrderEvent;
import com.nullwli.orderservice.service.OrderService;
import com.nullwli.playground.frameworks.starter.convention.result.Result;
import com.nullwli.playground.frameworks.starter.convention.result.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/api/order-service/order/create")
    public Result<OrderCreateRespDTO> OrderCreate(@RequestBody OrderCreateReqDTO requestPram) throws Exception {
        return Results.success(orderService.OrderCreate(requestPram));
    }

    @PostMapping("/api/order-service/order/update/pay-callback")
    public Result<Void> payCallbackUpdate(@RequestBody OrderUpdateReqDTO requestPram) {
        orderService.payCallbackUpdate(requestPram);
        return Results.success();
    }


    @PostMapping("/api/order-service/order/test/delay-close-order")
    public Result<Void> testDelayCloseOrder(@RequestBody DelayCloseOrderEvent requestPram) {
        orderService.testDelayCloseOrder(requestPram);
        return Results.success();
    }

    @PostMapping("/api/order-service/order/update/status")
    public Result<Void> updateStatus(@RequestBody OrderStatusUpdateReqDTO requestPram) {
        orderService.updateStatus(requestPram);
        return Results.success();
    }



}
