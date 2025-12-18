package com.eob.common.sms.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

/**
 * SMS 휴대폰 인증
 */
@Service
public class SmsService {

    /**
     * 세션에 저장할 Key 이름
     * (Member / Shop / Rider 공통 사용)
     */
    private static final String SESSION_SMS_CODE = "SMS_AUTH_CODE";
    private static final String SESSION_SMS_VERIFIED = "SMS_VERIFIED";

    /**
     * 휴대폰 인증번호 발송
     * - 6자리 인증번호 생성
     * - 세션에 저장
     * - SMS API로 문자 발송
     */
    public boolean sendAuthCode(String phone, HttpSession session){
        // 인증번호 생성 (6자리 숫자)
        String authCode = generateAuthCode();
        // 세션에 인증번호 저장
        session.setAttribute(SESSION_SMS_CODE, authCode);
        // 인증 상태 초기화 (재오청 대비)
        session.setAttribute(SESSION_SMS_VERIFIED, false);
        // SMS API로 문자 발송 (테스트용)
        System.out.println("[SMS 인증번호 테스트 출력] : "+authCode);
        return true;
    }

    /**
     * 휴대폰 인증번호 검증
     * - 세션에 저장된 인증번호와 비교
     */
    public boolean verifyAuthCode(String phone, String inputAuthCode, HttpSession session){
        // 세션에서 인증번호 조회
        Object savedCode = session.getAttribute(SESSION_SMS_CODE);
        // 인증번호가 없는 경우 (만료, 미발송)
        if(savedCode == null){
            return false;
        }

        // 입력한 인증번호와 세션 값 비교
        if(!savedCode.equals(inputAuthCode)){
            return false;
        }

        // 인증 성공 시, 세션에서 제거
        session.removeAttribute(SESSION_SMS_CODE);
        // 인증 성공 여부 표시
        session.setAttribute(SESSION_SMS_VERIFIED, true);
        
        return true;
    }

    /**
     * 6자리 인증번호 생성
     */
    private String generateAuthCode(){
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }
}
