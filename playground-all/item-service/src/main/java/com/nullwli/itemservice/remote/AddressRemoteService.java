package com.nullwli.itemservice.remote;

import com.nullwli.itemservice.remote.dto.AddressQueryRespDTO;
import com.nullwli.playground.frameworks.starter.convention.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service")
public interface AddressRemoteService {
    @PostMapping("/api/user-service/address/query/addressId")
    Result<AddressQueryRespDTO> AddressQuery(@RequestParam(value = "addressId") String addressId) ;
}
