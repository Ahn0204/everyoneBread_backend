package com.eob.member.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * SMS 인증 검증 요청 DTO
 * - 사용자 전화번호와 인증코드 받음
 * - 인증 검증용 : 인증번호 비교
 */
@Getter
@Setter
public class SmsVerifyRequest {
    private String phone;
    private String authCode;
}
