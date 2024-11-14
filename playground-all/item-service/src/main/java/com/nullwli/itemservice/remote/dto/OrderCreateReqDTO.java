package com.nullwli.itemservice.remote.dto;

import com.nullwli.itemservice.dto.domain.ItemPurchaseDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateReqDTO {

    private String userId;

    private String addressId;

    private String username;

    private Date orderTime;

    private List<ItemPurchaseDetailDTO> itemPurchaseDetails;

    private int originPrice;

    private int amountPaid;


}
