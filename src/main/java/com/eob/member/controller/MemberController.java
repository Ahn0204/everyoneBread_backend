package com.eob.member.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.eob.common.security.CustomSecurityDetail;
import com.eob.common.sms.dto.SmsSendRequest;
import com.eob.common.sms.dto.SmsVerifyRequest;
import com.eob.common.sms.service.SmsService;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.dto.RegisterRequest;
import com.eob.member.service.MemberService;
import com.eob.member.service.MypageService;
import com.eob.member.service.WishlistService;
import com.eob.order.model.data.OrderDetailResponse;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member/*")
public class MemberController {

    private final MemberService memberService;
    private final SmsService smsService;
    private final MypageService mypageService;
    private final WishlistService wishlistService;

    /*
     * 로그인 페이지
     */
    @GetMapping("login")
    public String loginPage() {
        return "member/member-login";
    }

    /*
     * 회원가입 유형 선택
     */
    @GetMapping("select")
    public String selectAccount() {
        return "member/member-select";
    }

    /*
     * 회원가입 페이지
     * ?role=USER 기본
     */
    @GetMapping("register") // 예솔: 파라미터 이름을 명시했습니다. name=role
    public String registerPage(@RequestParam(name = "role", defaultValue = "USER") String role, Model model) {
        RegisterRequest dto = new RegisterRequest();
        dto.setMemberRole(role); // 기본 USER
        model.addAttribute("registerRequest", dto);
        return "member/member-register";
    }

    /*
     * 회원가입 처리 (일반회원)
     */
    @PostMapping("register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest dto,
            BindingResult bindingResult, HttpSession session) {

        // 기본 유효성 검사
        if (bindingResult.hasErrors()) {
            return "member/member-register";
        }

        // SMS 인증 체크
        if (!smsService.isVerified(session)) {
            bindingResult.reject("sms.notVerified", "휴대폰 인증을 완료해주세요.");
            return "member/member-register";
        }

        // 회원 역할 강제 USER
        dto.setMemberRole("USER");

        // 일반 회원 가입 처리
        memberService.registerUser(dto, bindingResult, session);

        if (bindingResult.hasErrors()) {
            return "member/member-register";
        }

        // 인증 상태 초기화
        session.removeAttribute("SMS_VERIFIED");

        return "redirect:/member/login";
    }

    /*
     * 아이디 중복 확인
     */
    @GetMapping("check-id")
    @ResponseBody
    public boolean checkId(@RequestParam("memberId") String memberId) {
        return memberService.isMemberIdAvailable(memberId);
    }

    /*
     * 이메일 중복 확인
     */
    @GetMapping("check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam("memberEmail") String memberEmail) {
        return memberService.isMemberEmailAvailable(memberEmail);
    }

    /*
     * 아이디 찾기 - 휴대폰 인증번호 요청 (회원 존재 여부 체크)
     */
    @PostMapping("find/phone/send")
    @ResponseBody
    public ResponseEntity<?> findIdPhoneSend(
            @RequestBody SmsSendRequest request,
            HttpSession session) {
        // 이름 + 휴대폰으로 회원 존재 여부 확인
        if (!memberService.existsByNameAndPhone(request.getName(), request.getPhone())) {
            return ResponseEntity
                    .badRequest()
                    .body("일치하는 회원이 없습니다.");
        }

        // SMS 인증번호 발송 (기존 SmsService 재사용)
        smsService.sendAuthCode(request.getPhone(), session);

        // 아이디 찾기 목적 저장
        session.setAttribute("FIND_PURPOSE", "FIND_ID");

        return ResponseEntity.ok().build();
    }

    /*
     * 아이디 찾기 - 휴대폰 인증번호 확인
     */
    @PostMapping("find/phone/check")
    @ResponseBody
    public ResponseEntity<?> findIdPhoneCheck( @RequestBody SmsVerifyRequest request, HttpSession session) {
        
        // 인증번호 검증
        String result = smsService.verifyAuthCode(
            request.getPhone(),
            request.getAuthCode(),
            session);

        // 인증 실패
        if (!"SUCCESS".equals(result)) {
            return ResponseEntity
                    .badRequest()
                    .body("인증번호가 올바르지 않습니다.");
        }

        // 인증 성공 → 아이디 찾기 가능 상태로 세션 저장
        session.setAttribute("authVerified", true);
        session.setAttribute("authType", "PHONE");
        session.setAttribute("authValue", request.getPhone());

        return ResponseEntity.ok().build();
    }

    /*
     * 아이디 찾기 결과 페이지
     * - 인증 완료된 사용자만 접근 가능
     */
    @GetMapping("find/id/result")
    public String findIdResult(HttpSession session, Model model) {

        Boolean authVerified = (Boolean) session.getAttribute("authVerified");
        String purpose = (String) session.getAttribute("FIND_PURPOSE");

        // 인증 여부 + 목적 체크 (보안)
        if (authVerified == null || !"FIND_ID".equals(purpose)) {
            return "redirect:/member/login";
        }


        String phone = (String) session.getAttribute("authValue");

        // 휴대폰 기준으로 아이디 조회
        String memberId = memberService.findMemberIdByPhone(phone);

        model.addAttribute("memberId", memberId);

        // 아이디 조회 후 인증 세션 제거 (재사용 방지)
        session.removeAttribute("authVerified");
        session.removeAttribute("authType");
        session.removeAttribute("authValue");
        session.removeAttribute("FIND_PURPOSE");

        return "member/find-id-result";
    }

    /*
        비밀번호 찾기 - 휴대폰 인증번호 요청
    */
    @PostMapping("find/password/phone/send")
    @ResponseBody
    public ResponseEntity<?> findPwPhoneSend(
            @RequestBody SmsSendRequest request,
            HttpSession session
    ) {
        if (!memberService.existsByNameAndPhone(request.getName(), request.getPhone())) {
            return ResponseEntity.badRequest().body("일치하는 회원이 없습니다.");
        }

        smsService.sendAuthCode(request.getPhone(), session);

        // 비밀번호 재설정 목적 저장
        session.setAttribute("FIND_PURPOSE", "RESET_PW");

        return ResponseEntity.ok().build();
    }

    /*
        비밀번호 재설정 페이지
        - 인증 + RESET_PW 목적일 때만 접근 가능
    */
    @GetMapping("find/password/reset")
    public String resetPasswordPage(HttpSession session) {

        Boolean authVerified = (Boolean) session.getAttribute("authVerified");
        String purpose = (String) session.getAttribute("FIND_PURPOSE");

        if (authVerified == null || !"RESET_PW".equals(purpose)) {
            return "redirect:/member/login";
        }

        return "member/reset-password";
    }

    /*
        비밀번호 재설정 처리
    */
    @PostMapping("find/password/reset")
    @ResponseBody
    public ResponseEntity<?> resetPassword(
            @RequestParam String newPassword,
            HttpSession session
    ) {
        Boolean authVerified = (Boolean) session.getAttribute("authVerified");
        String purpose = (String) session.getAttribute("FIND_PURPOSE");

        // 인증 + 목적 체크
        if (authVerified == null || !"RESET_PW".equals(purpose)) {
            return ResponseEntity.status(403).body("인증이 필요합니다.");
        }

        String phone = (String) session.getAttribute("authValue");

        memberService.resetPasswordByPhone(phone, newPassword);

        // 인증 정보 완전 제거
        session.removeAttribute("authVerified");
        session.removeAttribute("authType");
        session.removeAttribute("authValue");
        session.removeAttribute("FIND_PURPOSE");

        return ResponseEntity.ok().build();
    }

    /**
     * 회원 정보 수정 (AJAX)
     * @param type
     * @param body
     * @param principal
     * @return
     */
    @PostMapping("mypage/info/update/{type}")
    @ResponseBody
    public ResponseEntity<?> updateMemberInfo(@PathVariable String type, @RequestBody Map<String, String> body, @AuthenticationPrincipal CustomSecurityDetail principal ) {
        // 요청 값 추출
        String value = body.get("value");

        // 값 비어있는지 체크
        if (value == null || value.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("result", "FAIL", "message", "값이 비어있습니다."));
        }

        // 로그인 회원 정보
        MemberEntity member = principal.getMember();

        // 유형별 처리
        switch (type) {
            case "phone":
                if (!value.matches("^010\\d{8}$")) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("result", "FAIL", "message", "휴대폰 번호 형식 오류"));
                }
                memberService.updatePhone(member.getMemberNo(), value);
                break;

            case "email":
                if (!value.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("result", "FAIL", "message", "이메일 형식 오류"));
                }
                if (!memberService.isMemberEmailAvailable(value)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("result", "FAIL", "message", "이미 사용 중인 이메일"));
                }
                memberService.updateEmail(member.getMemberNo(), value);
                break;

            default:
                return ResponseEntity.badRequest()
                        .body(Map.of("result", "FAIL", "message", "허용되지 않은 항목"));
        }

        return ResponseEntity.ok(Map.of("result", "OK"));
    }

    /**
     * 비밀번호 변경 (AJAX)
     * @param body
     * @param principal
     * @return
     */
    @PostMapping("mypage/password/change")
    @ResponseBody
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body, @AuthenticationPrincipal CustomSecurityDetail principal ) {
        // 요청 값 추출
        String currentPw = body.get("currentPw");
        String newPw = body.get("newPw");

        // 로그인 회원 정보
        MemberEntity member = principal.getMember();

        // 현재 비밀번호 확인
        if (!memberService.checkPassword(member, currentPw)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("result", "FAIL", "message", "현재 비밀번호가 틀렸습니다."));
        }

        // 비밀번호 변경
        memberService.changePassword(member.getMemberNo(), newPw);

        return ResponseEntity.ok(Map.of("result", "OK"));
    }

    /**
     * 회원 탈퇴
     */
    @PostMapping("mypage/withdraw")
    @ResponseBody
    public ResponseEntity<?> withdraw(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomSecurityDetail principal,
            HttpSession session
    ) {
        String reason = body.get("reason");

        if (reason == null || reason.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("result", "FAIL", "message", "탈퇴 사유 누락"));
        }

        MemberEntity member = principal.getMember();

        memberService.withdrawMember(member, reason);

        // 세션 종료
        session.invalidate();

        return ResponseEntity.ok(Map.of("result", "OK"));
    }

    /**
     * 마이페이지
     */
    @GetMapping("mypage")
    public String mypage() {
        return "redirect:/member/mypage/orderList";
    }

    /**
     * 마이페이지 - 주문 내역
     */
    @GetMapping("mypage/orderList")
    public String orderList(Model model) {

        // Security 인증 객체
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 로그인 사용자 정보
        CustomSecurityDetail user = (CustomSecurityDetail) authentication.getPrincipal();
        // 회원 번호
        Long memberNo = user.getMember().getMemberNo();

        model.addAttribute("menu", "orderList");
        model.addAttribute("orders", mypageService.getMyOrders(memberNo));

        return "member/mypage/orderList";
    }

    /**
     * 마이페이지 - 주문 상세 모달
     */
    @GetMapping("mypage/order-detail")
    public String orderDetailModal() {
        return "member/mypage/order-detail";
    }

    /**
     * 마이페이지 - 주문 상세 (AJAX)
     */
    @GetMapping("/orders/{orderNo}")
    @ResponseBody
    public OrderDetailResponse getOrderDetail(@PathVariable Long orderNo) {

        return mypageService.getOrderDetail(orderNo);
    }

    /**
     * 마이페이지 - 주문 취소 (AJAX)
     */
    @PostMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long orderNo,
            @AuthenticationPrincipal CustomSecurityDetail principal
    ) {
        Long memberNo = principal.getMember().getMemberNo();

        mypageService.cancelOrder(orderNo, memberNo);

        return ResponseEntity.ok().build();
    }

    /**
     * 마이페이지 - 즐겨찾기
     */
    @GetMapping("mypage/wishList")
    public String wishList(Model model) {
        // Security 인증 객체
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인 사용자 정보
        CustomSecurityDetail user = (CustomSecurityDetail) authentication.getPrincipal();

        // 회원 번호
        Long memberNo = user.getMember().getMemberNo();

        // 즐겨찾기 목록 조회
        model.addAttribute("wishlist", wishlistService.getMyActiveWishlist(memberNo));

        // 메뉴 활성화용
        model.addAttribute("menu", "wishlist");

        return "member/mypage/wishList";
    }

    /**
     * 마이페이지 - 후기
     */
    @GetMapping("mypage/reviewList")
    public String reviewList(@RequestParam(defaultValue = "0") int page, Model model) {

        // 1️. Spring Security 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2️. 로그인 사용자 정보 꺼내기
        CustomSecurityDetail user = (CustomSecurityDetail) authentication.getPrincipal();

        // 3️. 로그인 회원 번호 추출
        Long memberNo = user.getMember().getMemberNo();

        // 페이징 후기 조회
        var reviewPage = mypageService.getMyReviews(memberNo, page);

        // 서비스 호출 → 후기 리스트 조회
        model.addAttribute("reviewList", mypageService.getMyReviews(memberNo));
        // 페이징 정보
        model.addAttribute("page", reviewPage);
        // 마이페이지 메뉴 활성화용
        model.addAttribute("menu", "reviewList");

        // 6️. 후기 리스트 화면 이동
        return "member/mypage/reviewList";
    }

    /**
     * 마이페이지 - 후기 삭제 (AJAX)
     * 
     * 기능 설명
     * - 로그인한 회원의 후기만 삭제 가능
     * - 실제 삭제가 아닌 status = DELETED 처리 (soft delete)
     */
    @PostMapping("mypage/review/delete")
    @ResponseBody
    public ResponseEntity<String> deleteReview(@RequestParam Long reviewNo) {

        // 1️. Spring Security 인증 객체
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2️. 로그인 사용자 정보
        CustomSecurityDetail user = (CustomSecurityDetail) authentication.getPrincipal();

        // 3️. 로그인 회원 번호
        Long memberNo = user.getMember().getMemberNo();

        // 4️. 서비스 호출 (삭제 처리)
        mypageService.deleteMyReview(reviewNo, memberNo);

        // 성공 응답
        return ResponseEntity.ok("후기가 삭제되었습니다.");
    }

    /**
     * 마이페이지 - 문의
     */
    @GetMapping("mypage/qnaList")
    public String qnaList(Model model) {
        model.addAttribute("menu", "qnaList");
        return "member/mypage/qnaList";
    }

    /**
     * 마이페이지 - 배송지 관리
     */
    @GetMapping("mypage/deliveryList")
    public String delivery(Model model) {
        model.addAttribute("menu", "deliveryList");
        return "member/mypage/deliveryList";
    }

    /**
     * 마이페이지 - 개인정보수정
     */
    @GetMapping("mypage/info/check")
    public String info(Model model, HttpSession session) {

        // 개인정보수정 페이지 이동
        return "member/mypage/info-check";
    }

    /**
     * 마이페이지 - 개인정보수정 폼
     */
    @GetMapping("mypage/info")
    public String infoPage(Model model, HttpSession session) {

        if (session.getAttribute("INFO_AUTH") == null) {
            return "redirect:/member/mypage/info/check";
        }

        model.addAttribute("menu", "info");
        return "member/mypage/info";
    }

    /**
     * 마이페이지 - 개인정보수정 - 비밀번호 확인 (AJAX)
     */
    @PostMapping("mypage/info/check")
    @ResponseBody
    public ResponseEntity<?> infoCheckConfirm(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomSecurityDetail principal,
            HttpSession session
    ) {
        String password = body.get("password");

        if (!memberService.checkPassword(principal.getMember(), password)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("result", "FAIL", "message", "비밀번호가 일치하지 않습니다."));
        }

        // 인증 성공 → 세션 저장
        session.setAttribute("INFO_AUTH", true);

        return ResponseEntity.ok(Map.of("result", "OK"));
    }

}