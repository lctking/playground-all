package com.nullwli.itemservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nullwli.itemservice.dao.entity.ItemDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemMapper extends BaseMapper<ItemDO> {
}
