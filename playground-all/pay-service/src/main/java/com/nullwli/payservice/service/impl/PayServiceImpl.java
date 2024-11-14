package com.nullwli.payservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.nullwli.payservice.dto.req.PayCallbackReqDTO;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nullwli.payservice.config.AliPayProperties;
import com.nullwli.payservice.dao.entity.PayDO;
import com.nullwli.payservice.dao.mapper.PayMapper;
import com.nullwli.payservice.dto.base.AliPayRequest;
import com.nullwli.payservice.dto.base.PayResponse;
import com.nullwli.payservice.remote.OrderRemoteService;
import com.nullwli.payservice.remote.dto.OrderUpdateReqDTO;
import com.nullwli.payservice.service.PayService;
import com.nullwli.playground.frameworks.starter.convention.result.Result;
import com.nullwli.playground.frameworks.starter.convention.utils.BeanTools;
import com.nullwli.playground.frameworks.starter.convention.utils.DistributedIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayServiceImpl extends ServiceImpl<PayMapper, PayDO> implements PayService {
    private final AliPayProperties aliPayProperties;
    private final PayMapper payMapper;
    private static final DistributedIdGenerator distributedIdGenerator = new DistributedIdGenerator(1L);
    private final OrderRemoteService orderRemoteService;

    public static String generateId(String orderSn) {
        return distributedIdGenerator.generateId() + orderSn.substring(orderSn.length() - 6);
    }

    @SneakyThrows(value = Exception.class)
    @Override
    public PayResponse AliPay(AliPayRequest aliPayRequest){
        AlipayConfig alipayConfig = BeanTools.convert(aliPayProperties, AlipayConfig.class);
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(aliPayRequest.getOrderSn());
        model.setTotalAmount(aliPayRequest.getTotalAmount().toString());
        model.setSubject(aliPayRequest.getSubject());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayProperties.getNotifyUrl());
        request.setBizModel(model);
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            log.info("发起支付宝支付，订单号：{}，子订单号：{}，订单请求号：{}，订单金额：{} \n调用支付返回：\n\n{}\n",
                    aliPayRequest.getOrderSn(),
                    aliPayRequest.getOutOrderSn(),
                    aliPayRequest.getOrderRequestId(),
                    aliPayRequest.getTotalAmount(),
                    JSONObject.toJSONString(response));
            if (!response.isSuccess()) {
                throw new Exception("调用支付宝发起支付异常");
            }
            return new PayResponse(StrUtil.replace(StrUtil.replace(response.getBody(), "\"", "'"), "\n", ""));
        } catch (AlipayApiException ex) {
            throw new Exception("调用支付宝支付异常");
        }


    }

    @SneakyThrows(value = Exception.class)
    @Override
    public void AlipayNotify(Map<String, Object> requestParam) {
        //PayCallbackReqDTO payCallbackReqDTO = BeanUtils.mapToBean(requestParam, PayCallbackReqDTO.class);

        PayCallbackReqDTO build = PayCallbackReqDTO.builder()
                .orderSn(requestParam.get("out_trade_no").toString())
                .outOrderSn(requestParam.get("out_trade_no").toString())
                .channel("alipay")
                .tradeType("alipay")
                .subject(requestParam.get("subject").toString())
                .tradeNo(requestParam.get("trade_no").toString())
                .totalAmount((int) (Double.parseDouble(requestParam.get("total_amount").toString()) * 100))
                .gmtPayment(DateUtil.parse(requestParam.get("gmt_payment").toString()))
                .payAmount((int) (Double.parseDouble(requestParam.get("buyer_pay_amount").toString()) * 100))
                .status(requestParam.get("trade_status").toString())
                .orderRequestId(requestParam.get("out_trade_no").toString())
                .build();

        PayDO payDO = BeanTools.convert(build, PayDO.class);
        payDO.setPaySn(generateId(payDO.getOrderSn()));
        payMapper.insert(payDO);

        //TODO 2024-11-08晚mark 使用枚举类型来标准化pay-type和status
        //TODO 2024-11-10 将订单关闭逻辑放入消息队列，由order-service消费端完成
        OrderUpdateReqDTO orderUpdateReqDTO = OrderUpdateReqDTO.builder()
                .orderSn(payDO.getOrderSn())
                .payTime(payDO.getGmtPayment())
                .payType(1)
                .status(1).build();
        orderRemoteService.payCallbackUpdate(orderUpdateReqDTO);


    }

}
