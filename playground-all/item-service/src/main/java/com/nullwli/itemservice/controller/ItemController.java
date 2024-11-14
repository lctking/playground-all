package com.nullwli.itemservice.controller;



import com.nullwli.itemservice.dto.req.ItemCreateReqDTO;
import com.nullwli.itemservice.dto.req.ItemsPurchaseReqDTO;
import com.nullwli.itemservice.dto.resp.ItemCreateRespDTO;
import com.nullwli.itemservice.dto.resp.ItemQueryRespDTO;
import com.nullwli.itemservice.dto.resp.ItemsPurchaseRespDTO;
import com.nullwli.itemservice.remote.AddressRemoteService;
import com.nullwli.itemservice.remote.dto.AddressQueryRespDTO;
import com.nullwli.itemservice.service.ItemService;
import com.nullwli.playground.frameworks.starter.convention.result.Result;
import com.nullwli.playground.frameworks.starter.convention.result.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final AddressRemoteService addressRemoteService;


    @PostMapping("/api/item-service/item/create")
    public Result<ItemCreateRespDTO> ItemCreate(@RequestBody ItemCreateReqDTO requestPram) throws Exception {
        return Results.success(itemService.ItemCreate(requestPram));
    }
    @GetMapping("/api/item-service/item/query/itemId")
    public Result<ItemQueryRespDTO> SelectItemById(@RequestParam(value = "itemId") String itemId) throws Exception {
        return Results.success(itemService.SelectItemById(itemId));
    }


    @PostMapping("/api/item-service/item/purchase")
    public Result<ItemsPurchaseRespDTO> purchaseItems(@RequestBody ItemsPurchaseReqDTO requestPram) throws Exception {
        return Results.success(itemService.purchaseItems(requestPram));
    }

    @PostMapping("/api/item-service/item/test")
    public Result<AddressQueryRespDTO> test(@RequestParam(value = "addressId") String id) throws Exception {
        return Results.success(addressRemoteService.AddressQuery(id).getData());
    }

    @PostMapping("/api/item-service/item/flow-protection")
    public Result<Void> item(@RequestParam(value = "itemId") String itemId) {
        itemService.itemFlowProtection(itemId);
        return Results.success();
    }

    @PostMapping("/api/item-service/item/testRedisRollback")
    public Result<ItemsPurchaseRespDTO> testRedisRollback(@RequestBody ItemsPurchaseReqDTO requestPram) throws Exception {
        return Results.success(itemService.testRedisRollback(requestPram));
    }



}
