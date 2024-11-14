package com.nullwli.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nullwli.userservice.dao.entity.AddressDO;
import com.nullwli.userservice.dto.req.AddressCreateReqDTO;
import com.nullwli.userservice.dto.resp.AddressQueryRespDTO;

public interface AddressService extends IService<AddressDO> {
    void AddressCreate(AddressCreateReqDTO requestPram) throws Exception;

    AddressQueryRespDTO AddressSingleQuery(String addressId) throws Exception;
}
