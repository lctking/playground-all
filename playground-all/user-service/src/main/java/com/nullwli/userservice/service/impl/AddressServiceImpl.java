package com.nullwli.userservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nullwli.playground.frameworks.starter.convention.utils.BeanTools;
import com.nullwli.userservice.dao.entity.AddressDO;
import com.nullwli.userservice.dao.mapper.AddressMapper;
import com.nullwli.userservice.dto.req.AddressCreateReqDTO;
import com.nullwli.userservice.dto.resp.AddressQueryRespDTO;
import com.nullwli.userservice.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<AddressMapper,AddressDO> implements AddressService {
    private final AddressMapper addressMapper;


    @Override
    public void AddressCreate(AddressCreateReqDTO requestPram) throws Exception {
        AddressDO addressDO = null;
        addressDO = BeanTools.convert(requestPram, AddressDO.class);

        addressDO.setUserId(Long.parseLong(requestPram.getUserId()));

        addressMapper.insert(addressDO);
    }

    @Override
    public AddressQueryRespDTO AddressSingleQuery(String addressId) throws Exception {
        AddressDO addressDO = addressMapper.selectById(Long.parseLong(addressId));
        return BeanTools.convert(addressDO,AddressQueryRespDTO.class);
    }
}
