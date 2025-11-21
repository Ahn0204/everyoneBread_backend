package com.eob.member.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * SMS 인증 요청 DTO
 * - 사용자 전화번호만 받음
 * - 문자 발송용
 */
@Getter
@Setter
public class SmsSendRequest {
    private String phone;
}
