package com.nullwli.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nullwli.orderservice.dao.entity.OrderDO;
import com.nullwli.orderservice.dto.req.OrderCreateReqDTO;
import com.nullwli.orderservice.dto.req.OrderStatusUpdateReqDTO;
import com.nullwli.orderservice.dto.req.OrderUpdateReqDTO;
import com.nullwli.orderservice.dto.resp.OrderCreateRespDTO;
import com.nullwli.orderservice.mq.event.DelayCloseOrderEvent;

public interface OrderService extends IService<OrderDO> {
    OrderCreateRespDTO OrderCreate(OrderCreateReqDTO requestPram) throws Exception;

    void payCallbackUpdate(OrderUpdateReqDTO requestPram);

    void testDelayCloseOrder(DelayCloseOrderEvent requestPram);

    void updateStatus(OrderStatusUpdateReqDTO requestPram);
}
