
package com.nullwli.itemservice.mq.event;

import com.nullwli.itemservice.dto.req.ItemPurchaseDetailReqDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 延迟关闭订单事件
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelayCloseOrderEvent {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 商品购买信息
     */
    private List<ItemPurchaseDetailReqDTO> itemsPurchaseDetails;
}
