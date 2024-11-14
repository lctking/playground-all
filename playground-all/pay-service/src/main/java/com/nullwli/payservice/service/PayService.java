package com.nullwli.payservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nullwli.payservice.dao.entity.PayDO;
import com.nullwli.payservice.dto.base.AliPayRequest;
import com.nullwli.payservice.dto.base.PayResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PayService extends IService<PayDO> {
    PayResponse AliPay(AliPayRequest aliPayRequest) throws Exception;

    void AlipayNotify(Map<String, Object> requestParam) throws Exception;
}
