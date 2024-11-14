package com.nullwli.userservice.controller;

import com.nullwli.playground.frameworks.starter.convention.result.Result;
import com.nullwli.playground.frameworks.starter.convention.result.Results;
import com.nullwli.userservice.dto.req.AddressCreateReqDTO;
import com.nullwli.userservice.dto.resp.AddressQueryRespDTO;
import com.nullwli.userservice.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/api/user-service/address/create")
    public Result<Void> AddressCreate(@RequestBody AddressCreateReqDTO requestPram) throws Exception {
        addressService.AddressCreate(requestPram);
        return Results.success();
    }

    @PostMapping("/api/user-service/address/query/addressId")
    public Result<AddressQueryRespDTO> AddressQuery(@RequestParam(value = "addressId") String addressId) throws Exception {
        return Results.success(addressService.AddressSingleQuery(addressId));
    }

//    @PostMapping("/api/user-service/address/query/remote/addressId")
//    public Result<AddressQueryRespDTO> AddressRemoteQuery(@RequestBody PurchaserAddressIdDTO purchaserAddressIdDTO) throws Exception {
//        return Results.success(addressService.AddressSingleQuery(purchaserAddressIdDTO.getAddressId()));
//    }
}
