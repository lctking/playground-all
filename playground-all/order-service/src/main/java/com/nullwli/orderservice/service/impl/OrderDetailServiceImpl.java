package com.nullwli.orderservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nullwli.orderservice.dao.entity.OrderDetailDO;
import com.nullwli.orderservice.dao.mapper.OrderDetailMapper;
import com.nullwli.orderservice.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetailDO> implements OrderDetailService {
}
