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
        String result = smsService.sendAuthCode(request.getPhone(), session);
    
        // 결과에 따른 메시지 분기
        boolean success = false;
        String message;

        switch (result){
            case "SUCCESS":
                success = true;
                message = "인증번호가 발송되었습니다.";
                break;

            case "COOLDOWN":
                message = "1분 후 다시 시도해주세요.";
                break;

            case "MAX_COUNT":
                message = "인증 요청 횟수를 초과했습니다.";
                break;

            default:
                message = "인증번호 발송에 실패했습니다.";
        }

        // 성공 여부 + 메시지 응답
        response.put("success", success);
        response.put("message", message);

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
        String result = smsService.verifyAuthCode(request.getPhone(), request.getAuthCode(), session);

        // 검증 결과에 따른 메시지 분기
        boolean success = false;
        String message;

        switch(result){
            case "SUCCESS":
                success = true;
                message = "휴대폰 인증이 완료되었습니다.";
                break;

            case "EXPIRED":
                message = "인증번호가 만료되었습니다. 다시 요청해주세요.";
                break;

            case "MISMATCH":
                message = "인증번호가 일치하지 않습니다.";
                break;

            default:
                message = "인증번호를 다시 요청해주세요.";            
        }

        // 검증 결과 응답 (성공 여부 + 메시지)
        response.put("success", success);
        response.put("message", message);

        return response;
    }

}
