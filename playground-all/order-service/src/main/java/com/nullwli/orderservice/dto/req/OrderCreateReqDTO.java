package com.nullwli.orderservice.dto.req;

import com.nullwli.orderservice.remote.dto.ItemPurchaseDetailDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class OrderCreateReqDTO {

    private String userId;

    private String addressId;

    private String username;

    private Date orderTime;

    private List<ItemPurchaseDetailDTO> itemPurchaseDetails;

    private int originPrice;

    private int amountPaid;
}
