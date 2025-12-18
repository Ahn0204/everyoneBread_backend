package com.eob.common.sms.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eob.common.sms.dto.SmsSendRequest;
import com.eob.common.sms.dto.SmsVerifyRequest;
import com.eob.common.sms.service.SmsService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    /**
     * 휴대폰 인증번호 전송
     * POST /sms/send
     * body : { "phone": "01012345678" }
     */
    @PostMapping("/send")
    public Map<String, Object> send(@RequestBody SmsSendRequest request, HttpSession session){
        // 인증번호 생성 및 SMS 전송
        Map<String, Object> response = new HashMap<>();
        // SMS 전송 성공 여부
        boolean success = smsService.sendAuthCode(request.getPhone(), session);
        // 인증번호 생성 및 전송 요청
        response.put("success", success);
        // SMS 전송 결과 응답
        return response;
    }

    /**
     * 휴대폰 인증번호 확인
     * POST /sms/check
     * body: { "phone": "01012345678", "authCode": "123456" }
     */
    @PostMapping("/check")
    public Map<String, Object> check(@RequestBody SmsVerifyRequest request, HttpSession session){
        // 인증번호 확인
        Map<String, Object> response = new HashMap<>();
        // SMS 인증번호 검증
        boolean success = smsService.verifyAuthCode(request.getPhone(), request.getAuthCode(), session);
        // 검증 결과 응답
        response.put("success", success);

        return response;
    }

}
