package com.nullwli.itemservice.service.handler.item.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResultDTO {
    private Boolean isSuccess;

    private Boolean tokenHasNone;

    private List<String> NoneTokenInfo;
}
