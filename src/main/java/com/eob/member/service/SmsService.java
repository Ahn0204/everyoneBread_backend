// package com.eob.member.service;

// import com.google.gson.JsonObject;
// import lombok.RequiredArgsConstructor;
// import okhttp3.*;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;

// import jakarta.servlet.http.HttpSession;
// import java.util.Base64;
// import java.util.Random;

// import javax.crypto.Mac;
// import javax.crypto.spec.SecretKeySpec;

// @Service
// @RequiredArgsConstructor
// public class SmsService {

//     @Value("${sms.api-key}")
//     private String apiKey;

//     @Value("${sms.api-secret}")
//     private String apiSecret;

//     @Value("${sms.from-phone}")
//     private String fromPhone;

//     private final OkHttpClient client = new OkHttpClient();

//     /**
//      * 인증번호 생성 + API 호출 + 세션 저장
//      */
//     public String sendAuthCode(String phone, HttpSession session) {

//         try {
//             // 1) 인증번호 생성
//             String authCode = String.format("%06d", new Random().nextInt(1000000));

//             // 2) 요청 바디 JSON
//             JsonObject body = new JsonObject();
//             body.addProperty("type", "SMS");
//             body.addProperty("from", fromPhone);
//             body.addProperty("to", phone);
//             body.addProperty("text", "[모두의빵] 인증번호는 " + authCode + " 입니다.");

//             // 3) 현재 시간 (timestamp)
//             String timestamp = String.valueOf(System.currentTimeMillis());

//             String salt = generateSalt();

//             // 4) signature 생성
//             String signature = makeSignature(timestamp + salt);

//             String authorizationHeader =
//                     "HMAC-SHA256 apiKey=" + apiKey +
//                     ", date=" + timestamp +
//                     ", salt=" + salt +
//                     ", signature=" + signature;


//             // 5) HTTP 요청 생성
//             Request request = new Request.Builder()
//                     .url("https://api.coolsms.co.kr/messages/v4/send")
//                     .addHeader("Authorization", authorizationHeader)
//                     .addHeader("Content-Type", "application/json; charset=utf-8")
//                     .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
//                     .build();

//             // 6) API 호출
//             Response response = client.newCall(request).execute();

//             System.out.println("Authorization:" + authorizationHeader);
//             System.out.println("Response Body:" +response.peekBody(Long.MAX_VALUE).string());

//             if (!response.isSuccessful()) {
//                 System.out.println("SMS 전송 실패: " + response.body().string());
//                 return null;
//             }

//             // 7) 세션 저장
//             session.setAttribute("AUTH_CODE_" + phone, authCode);
//             session.setAttribute("AUTH_EXPIRE_" + phone,
//                     System.currentTimeMillis() + (3 * 60 * 1000));

//             return authCode;

//         } catch (Exception e) {
//             e.printStackTrace();
//             return null;
//         }
//     }

//     private String generateSalt(){
//         return Long.toHexString(Double.doubleToLongBits(Math.random()));
//     }

//     private String makeSignature(String data) throws Exception {

//         SecretKeySpec signingKey = new SecretKeySpec(apiSecret.getBytes("UTF-8"), "HmacSHA256");
//         Mac mac = Mac.getInstance("HmacSHA256");
//         mac.init(signingKey);
//         byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));

//         return Base64.getEncoder().encodeToString(rawHmac);
//     }

//     /**
//      * 인증번호 검증
//      */
//     public boolean verifyAuthCode(String phone, String inputCode, HttpSession session) {

//         String savedCode = (String) session.getAttribute("AUTH_CODE_" + phone);
//         Long expire = (Long) session.getAttribute("AUTH_EXPIRE_" + phone);

//         // 세션이 아예 없음 → 인증 안 됨
//         if (savedCode == null || expire == null) {
//             return false;
//         }

//         // 만료 시간 체크
//         if (System.currentTimeMillis() > expire) {
//             return false;
//         }

//         // 코드 비교
//         boolean isMatch = savedCode.equals(inputCode);

//         // 성공하면 세션에 인증 완료 표시 (선택)
//         if (isMatch) {
//             session.setAttribute("AUTH_OK_" + phone, "OK");
//         }

//         return isMatch;
//     }
// }
