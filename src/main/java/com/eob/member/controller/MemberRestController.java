// package com.eob.member.controller;

// import com.eob.member.model.dto.SmsSendRequest;
// import com.eob.member.model.dto.SmsVerifyRequest;
// import com.eob.member.service.SmsService;
// import jakarta.servlet.http.HttpSession;
// import lombok.RequiredArgsConstructor;

// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/member")
// public class MemberRestController {

//     private final SmsService smsService;

//     /**
//      * 휴대폰 인증번호 발송
//      * URL: POST /member/send-auth-code
//      * JSON: { "phone": "01012345678" }
//      */
//     @PostMapping("/send-auth-code")
//     public Map<String, Object> sendAuthCode(@RequestBody SmsSendRequest request, HttpSession session) {

//         String phone = request.getPhone();

//         String authCode = smsService.sendAuthCode(phone, session);

//         Map<String, Object> result = new HashMap<>();

//         if (authCode == null) {
//             result.put("success", false);
//             return result;
//         }

//         result.put("success", true);

//         // 개발 중에는 실 코드도 보내주면 프론트 테스트에 좋음
//         // 운영 시에는 반드시 삭제!!!
//         result.put("authCode", authCode);

//         return result;
//     }


//     /**
//      * 인증번호 검증
//      * URL: POST /member/verify-auth-code
//      * JSON: { "phone": "01012345678", "authCode": "123456" }
//      */
//     @PostMapping("/verify-auth-code")
//     public Map<String, Object> verifyAuthCode(@RequestBody SmsVerifyRequest request, HttpSession session) {

//         boolean result = smsService.verifyAuthCode(
//                 request.getPhone(),
//                 request.getAuthCode(),
//                 session
//         );

//         Map<String, Object> map = new HashMap<>();
//         map.put("success", result);

//         if(result) {
//             // 인증 성공 시 세션에 인증 완료 표시
//             session.setAttribute("AUTH_OK_" + request.getPhone(), "OK");

//             // 인증 번호 사용 후 삭제
//             session.removeAttribute("AUTH_CODE_" + request.getPhone());
//         }

//         return map;
//     }

// }
