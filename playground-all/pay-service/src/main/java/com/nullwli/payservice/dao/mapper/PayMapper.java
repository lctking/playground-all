package com.nullwli.payservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nullwli.payservice.dao.entity.PayDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayMapper extends BaseMapper<PayDO> {
}
