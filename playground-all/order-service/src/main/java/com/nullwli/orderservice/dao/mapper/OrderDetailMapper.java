package com.nullwli.orderservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nullwli.orderservice.dao.entity.OrderDetailDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetailDO> {
}
