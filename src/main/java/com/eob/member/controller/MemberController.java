package com.eob.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eob.member.model.data.MemberEntity;
import com.eob.member.service.MemberService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
@RequiredArgsConstructor
@RequestMapping("/member/*")
public class MemberController {

    private final MemberService memberService;

    // 로그인 페이지
    // localhost:8080/member/login
    @GetMapping("login")
    public String loginPage() {
        return "member/member-login";
    }

    // 계정 유형 선택
    @GetMapping("select")
    public String selectAccount() {
        return "member/member-select";
    }
    
    // 회원가입
    @GetMapping("register")
    public String register(@RequestParam(name="role", defaultValue = "USER") String role, Model model) {
        model.addAttribute("role", role);
        return "member/member-register";
    }

    // 회원가입
    @PostMapping("register")
    public String register(MemberEntity member) {
        // 유효성 검사 - 추가 예정

        // DB 저장
        memberService.saveMember(member);

        // 비즈니스 계정(SELLER)이면 상점 등록 페이지로 이동
        if("SELLER".equals(member.getMemberRole())){
            return "redirect:/shop/shop-register";
        }

        // 일반 계정이면 로그인 페이지로 이동
        return "redirect:/member/member-login";
    }

    // 아이디 중복 확인
    @GetMapping("check-id")
    @ResponseBody
    public boolean checkId(@RequestParam("memberId") String memberId){
        return memberService.isMemberIdAvailable(memberId);
    }

    // 이메일 중복 확인
    @GetMapping("check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam("memberEmail") String memberEmail){
        return memberService.isMemberEmailAvailable(memberEmail);
    }

    // 휴대폰 인증번호 발송
    @PostMapping("send-auth-code")
    @ResponseBody
    public String sendAuthCode(@RequestBody String memberPhone) {
        // 인증번호 생성 ( 6자리)
    //    String authCode = smsService.sendAuthCode(memberPhone);
        return "123456"; // 예시로 고정된 인증번호 반환
    }
}