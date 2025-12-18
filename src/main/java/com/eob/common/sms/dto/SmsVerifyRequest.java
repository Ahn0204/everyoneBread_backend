package com.eob.common.sms.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// SMS 인증 요청 DTO
@Getter
@Setter
@NoArgsConstructor
public class SmsVerifyRequest {
    /**
     * 휴대폰 번호
     */
    private String phone;

    /**
     * 인증 코드
     */
    private String authCode;

}
