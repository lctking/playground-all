package com.nullwli.payservice.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import com.alibaba.nacos.api.model.v2.Result;
import com.nullwli.payservice.dto.base.PayResponse;
import com.nullwli.payservice.service.PayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nullwli.payservice.dto.base.AliPayRequest;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PayController {
    private final PayService payService;


    @PostMapping("/api/pay-service/alipay")
    public Result<PayResponse> AliPay(@RequestBody AliPayRequest aliPayRequest) throws Exception {
        return Result.success(payService.AliPay(aliPayRequest));
    }

//    @PostMapping("/api/pay-service/notify")
//    public void AliPayNotify(@RequestBody HttpServletRequest request) throws Exception {
//        return Result.success(payService.AlipayNotify(request));
//    }

    @PostMapping("/api/pay-service/alipay-notify")
    public void AliPayNotify(@RequestParam Map<String, Object> requestParam) throws Exception {
        log.info("进入回调逻辑,requestParam = {}",requestParam);
        payService.AlipayNotify(requestParam);
    }
}
