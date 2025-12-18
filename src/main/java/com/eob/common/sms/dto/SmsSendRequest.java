package com.eob.common.sms.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// SMS 발송 요청 DTO
@Getter
@Setter
@NoArgsConstructor
public class SmsSendRequest {
    /**
     * 휴대폰 번호
     */
    private String phone;

}
