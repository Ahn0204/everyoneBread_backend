package com.eob.member.service;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${sms.api-key}")
    private String apiKey;

    @Value("${sms.api-secret}")
    private String apiSecret;

    @Value("${sms.from-phone}")
    private String fromPhone;

    private final OkHttpClient client = new OkHttpClient();

    /**
     * 인증번호 생성 + API 호출 + 세션 저장
     */
    public String sendAuthCode(String phone, HttpSession session) {

        // 1) 6자리 인증번호 생성
        String authCode = String.format("%06d", new Random().nextInt(1000000));

        // 2) 요청 JSON 구성
        JsonObject body = new JsonObject();
        body.addProperty("type", "SMS");
        body.addProperty("from", fromPhone);
        body.addProperty("to", phone);
        body.addProperty("text", "[모두의빵] 인증번호는 " + authCode + " 입니다.");

        // 3) HTTP 요청 구성
        Request request = new Request.Builder()
                .url("https://api.coolsms.co.kr/messages/v4/send")
                .addHeader("Authorization", Credentials.basic(apiKey, apiSecret)) // 테스트 가능
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        // 4) API 호출
        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                System.out.println("SMS 전송 실패: " + response);
                return null;
            }

            // 5) 세션 저장 구조 (핸드폰 번호별 저장)
            session.setAttribute("AUTH_CODE_" + phone, authCode);
            session.setAttribute("AUTH_EXPIRE_" + phone,
                    System.currentTimeMillis() + (3 * 60 * 1000)); // 3분

            System.out.println("SMS 인증번호 전송: " + authCode);

            return authCode;

        } catch (IOException e) {
            System.out.println("SMS API 에러: " + e.getMessage());
            return null;
        }
    }

    /**
     * 인증번호 검증
     */
    public boolean verifyAuthCode(String phone, String inputCode, HttpSession session) {

        String savedCode = (String) session.getAttribute("AUTH_CODE_" + phone);
        Long expire = (Long) session.getAttribute("AUTH_EXPIRE_" + phone);

        // 세션이 아예 없음 → 인증 안 됨
        if (savedCode == null || expire == null) {
            return false;
        }

        // 만료 시간 체크
        if (System.currentTimeMillis() > expire) {
            return false;
        }

        // 코드 비교
        boolean isMatch = savedCode.equals(inputCode);

        // 성공하면 세션에 인증 완료 표시 (선택)
        if (isMatch) {
            session.setAttribute("AUTH_OK_" + phone, "OK");
        }

        return isMatch;
    }
}
