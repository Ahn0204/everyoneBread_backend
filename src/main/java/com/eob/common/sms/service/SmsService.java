package com.eob.common.sms.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eob.common.sms.util.SmsSender;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


/**
 * SMS 휴대폰 인증
 * - Member / Shop / Rider 공통 사용
 * - 세션 기반
 */
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsSender smsSender;

    @Value("${coolsms.from-number}")
    private String fromNumber;

    /**
     * 세션에 저장할 Key 이름
     * (Member / Shop / Rider 공통 사용)
     */
    private static final String SESSION_SMS_CODE = "SMS_AUTH_CODE";
    private static final String SESSION_SMS_VERIFIED = "SMS_VERIFIED";
    private static final String SESSION_SMS_TIME = "SMS_AUTH_TIME";

    /**
     * 재요청 제한 관련
     */
    private static final String SESSION_SMS_COUNT = "SMS_SEND_COUNT";       // 발송 횟수
    private static final String SESSION_SMS_LAST_TIME = "SMS_LAST_TIME";    // 마지막 발송 시각
    private static final int SMS_MAX_COUNT = 5;                             // 최대 발송 횟수
    private static final long SMS_COOLDOWN_TIME = 60 * 1000;                // 60초 쿨타임

    /**
     * 인증 만료 시간 3분
     */
    private static final long SMS_EXPIRE_TIME = 3 * 60 * 1000;

    /**
     * 발송 결과 코드
     */
    private static final String SEND_SUCCESS = "SUCCESS";
    private static final String SEND_COOLDOWN = "COOLDOWN";
    private static final String SEND_MAX_COUNT = "MAX_COUNT";
    private static final String SEND_FAIL = "SEND_FAIL";

    /**
     * 검증 결과 코드
     */
    private static final String VERIFY_SUCCESS = "SUCCESS";
    private static final String VERIFY_EXPIRED = "EXPIRED";
    private static final String VERIFY_MISMATCH = "MISMATCH";
    private static final String VERIFY_NOT_FOUND = "NOT_FOUND";    

    /**
     * 휴대폰 인증번호 발송
     * - 6자리 인증번호 생성
     * - 세션에 저장
     * - SMS API로 문자 발송
     */
    public String sendAuthCode(String phone, HttpSession session){

        // 재요청 제한 체크
        Integer count = (Integer) session.getAttribute(SESSION_SMS_COUNT);
        Long lastTime = (Long) session.getAttribute(SESSION_SMS_LAST_TIME);
        
        if(count == null) count = 0;
        
        // 최대 횟수 초과
        if(count >= SMS_MAX_COUNT){
            return SEND_MAX_COUNT;
        }
        
        // 쿨타임 체크
        if(lastTime != null && System.currentTimeMillis() - lastTime < SMS_COOLDOWN_TIME){
            return SEND_COOLDOWN;
        }

        // 인증번호 생성 (6자리 숫자)
        String authCode = generateAuthCode();

        try{
            // 세션에 인증번호 저장
            session.setAttribute(SESSION_SMS_CODE, authCode);
            // 인증 만료 시간 3분
            session.setAttribute(SESSION_SMS_TIME, System.currentTimeMillis());
            // 인증 상태 초기화 (재요청 대비)
            session.setAttribute(SESSION_SMS_VERIFIED, false);
            
            // 문자 내용 구성
            String message = "[모두의 빵] 인증번호는 [" + authCode + "] 입니다. (3분 이내 입력)";
            // SMS 발송
            // 실제 구동 시 주석 해제 해야 함
            // smsSender.sendSms(phone, fromNumber, message);

            // 발송 성공 -> 횟수 증가
            session.setAttribute(SESSION_SMS_COUNT, count + 1);
            session.setAttribute(SESSION_SMS_LAST_TIME, System.currentTimeMillis());

            // SMS API로 문자 발송 (테스트용)
            System.out.println("[SMS 인증번호 테스트 출력] : "+authCode);

            return SEND_SUCCESS;

        }catch(Exception e){
            e.printStackTrace();
            return SEND_FAIL;
        }
    }

    /**
     * 휴대폰 인증번호 검증
     * - 세션에 저장된 인증번호와 비교
     */
    public String verifyAuthCode(String phone, String inputAuthCode, HttpSession session){

        // 세션에서 인증번호 조회
        Object savedCode = session.getAttribute(SESSION_SMS_CODE);
        // 인증 시간 만료
        Long sendTime = (Long) session.getAttribute(SESSION_SMS_TIME);
        
        // 인증번호 없음 (만료, 미발송)
        if(savedCode == null || sendTime == null){
            return VERIFY_NOT_FOUND;
        }

        // 인증번호 만료
        if(System.currentTimeMillis() - sendTime > SMS_EXPIRE_TIME){
            session.removeAttribute(SESSION_SMS_CODE);
            session.removeAttribute(SESSION_SMS_TIME);
            return VERIFY_EXPIRED;
        }

        // 입력한 인증번호와 세션 값 비교
        if(!savedCode.equals(inputAuthCode)){
            return VERIFY_MISMATCH;
        }

        // 인증 성공 처리
        session.removeAttribute(SESSION_SMS_CODE);
        session.removeAttribute(SESSION_SMS_TIME);
        session.removeAttribute(SESSION_SMS_COUNT);
        session.removeAttribute(SESSION_SMS_LAST_TIME);
        session.setAttribute(SESSION_SMS_VERIFIED, true);
        
        return VERIFY_SUCCESS;
    }

    /**
     * 인증 성공 여부 조회 (회원가입 시 사용)
     */
    public boolean isVerified(HttpSession session){
        Object verified = session.getAttribute(SESSION_SMS_VERIFIED);
        return verified != null && (boolean) verified;
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