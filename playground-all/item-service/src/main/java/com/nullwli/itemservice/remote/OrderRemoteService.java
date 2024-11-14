package com.nullwli.itemservice.remote;

import com.nullwli.itemservice.remote.dto.OrderCreateReqDTO;
import com.nullwli.itemservice.remote.dto.OrderCreateRespDTO;
import com.nullwli.itemservice.remote.dto.OrderStatusUpdateReqDTO;
import com.nullwli.playground.frameworks.starter.convention.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(value = "order-service")
public interface OrderRemoteService {
    @PostMapping("/api/order-service/order/create")
    Result<OrderCreateRespDTO> OrderCreate(@RequestBody OrderCreateReqDTO requestPram);

    @PostMapping("/api/order-service/order/update/status")
    Result<Void> updateStatus(@RequestBody OrderStatusUpdateReqDTO requestPram);

}
