package com.nullwli.payservice.remote;

import com.nullwli.payservice.remote.dto.OrderUpdateReqDTO;
import com.nullwli.playground.frameworks.starter.convention.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "order-service")
public interface OrderRemoteService {

    @PostMapping("/api/order-service/order/update/pay-callback")
    Result<Void> payCallbackUpdate(@RequestBody OrderUpdateReqDTO requestPram);
}
