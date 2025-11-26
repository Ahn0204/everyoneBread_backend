package com.eob.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.eob.member.model.dto.RegisterRequest;
import com.eob.member.service.MemberService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member/*")
public class MemberController {

    private final MemberService memberService;

    // 로그인 페이지
    @GetMapping("login")
    public String loginPage() {
        return "member/member-login";
    }

    // 계정 유형 선택
    @GetMapping("select")
    public String selectAccount() {
        return "member/member-select";
    }

    // 회원가입 페이지
    @GetMapping("register")
    public String registerPage(@RequestParam(defaultValue = "USER") String role, Model model) {
        RegisterRequest dto = new RegisterRequest();
        dto.setMemberRole(role);
        model.addAttribute("registerRequest", dto);
        return "member/member-register";
    }

    // 회원가입 처리
    @PostMapping("register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest dto,
            BindingResult bindingResult,
            HttpSession session
    ) {
        System.out.println(" 전달된 memberROle:" +dto.getMemberRole());

        // 1차 DTO @Valid 검증
        if (bindingResult.hasErrors()) {
            return "member/member-register";
        }

        // 서비스 로직 실행 → bindingResult에 오류 넣을 수 있음
        memberService.register(dto, session, bindingResult);

        if (bindingResult.hasErrors()) {
            return "member/member-register";
        }

        // 상점 회원은 shop-register로 이동
        if ("SHOP".equals(dto.getMemberRole())) {
            return "redirect:/shop/shop-register-start";
        }

        // 라이더 회원은 rider-register로 이동
        if("RIDER".equals(dto.getMemberRole())){
            return "redirect:/rider/register-start";
        }

        // 일반 회원은 member-register로 이동
        if("USER".equals(dto.getMemberRole())){
            return "redirect:/member/login";
        }

        // 일반 회원은 로그인으로 이동
        return "redirect:/member/login";
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
}
