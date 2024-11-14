package com.nullwli.itemservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nullwli.itemservice.dao.entity.ItemDO;
import com.nullwli.itemservice.dto.req.ItemCreateReqDTO;
import com.nullwli.itemservice.dto.req.ItemsPurchaseReqDTO;
import com.nullwli.itemservice.dto.resp.ItemCreateRespDTO;
import com.nullwli.itemservice.dto.resp.ItemQueryRespDTO;
import com.nullwli.itemservice.dto.resp.ItemsPurchaseRespDTO;

public interface ItemService extends IService<ItemDO> {
    ItemCreateRespDTO ItemCreate(ItemCreateReqDTO requestPram) throws Exception;

    ItemQueryRespDTO SelectItemById(String itemId) throws Exception;

    ItemsPurchaseRespDTO purchaseItems(ItemsPurchaseReqDTO requestPram) throws Exception;

    void itemFlowProtection(String itemId);

    ItemsPurchaseRespDTO testRedisRollback(ItemsPurchaseReqDTO requestPram) throws Exception;
}
