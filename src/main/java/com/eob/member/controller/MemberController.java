package com.eob.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.eob.member.model.dto.RegisterRequest;
import com.eob.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member/*")
public class MemberController {

    private final MemberService memberService;

    /*
        로그인 페이지
    */
    @GetMapping("login")
    public String loginPage() {
        return "member/member-login";
    }

    /*
        회원가입 유형 선택
    */
    @GetMapping("select")
    public String selectAccount() {
        return "member/member-select";
    }

    /*
        회원가입 페이지
        ?role=USER 기본
    */
    @GetMapping("register") //예솔: 파라미터 이름을 명시했습니다. name=role
    public String registerPage(@RequestParam(name="role",defaultValue = "USER") String role, Model model) {
        RegisterRequest dto = new RegisterRequest();
        dto.setMemberRole(role);  // 기본 USER
        model.addAttribute("registerRequest", dto);
        return "member/member-register";
    }

    /*
        회원가입 처리 (일반회원)
    */
    @PostMapping("register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest dto,
            BindingResult bindingResult
    ) {
        // Valid에서 걸리면 다시 보여줌
        if (bindingResult.hasErrors()) {
            return "member/member-register";
        }

        // 회원 역할 강제 USER
        dto.setMemberRole("USER");

        // 일반 회원 가입 처리
        memberService.registerUser(dto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "member/member-register";
        }

        return "redirect:/member/login";
    }

    /*
        아이디 중복 확인
    */
    @GetMapping("check-id")
    @ResponseBody
    public boolean checkId(@RequestParam("memberId") String memberId){
        return memberService.isMemberIdAvailable(memberId);
    }

    /*
        이메일 중복 확인
    */
    @GetMapping("check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam("memberEmail") String memberEmail){
        return memberService.isMemberEmailAvailable(memberEmail);
    }
}
