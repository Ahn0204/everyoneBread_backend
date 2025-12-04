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

    /**
     * 마이페이지
     */
    @GetMapping("mypage")
    public String mypage(){
        return "redirect:/member/mypage/orderList";
    }

    /**
     * 마이페이지 - 주문 내역
     */
    @GetMapping("mypage/orderList")
    public String orderList(Model model){
        model.addAttribute("menu","orderList");
        return "member/mypage/orderList";
    }

    /**
     * 마이페이지 - 즐겨찾기
     */
    @GetMapping("mypage/wishList")
    public String wishList(Model model) {
        model.addAttribute("menu", "wishList");
        return "member/mypage/wishList";
    }

    /**
     * 마이페이지 - 후기
     */
    @GetMapping("mypage/reviewList")
    public String reviewList(Model model) {
        model.addAttribute("menu", "reviewList");
        return "member/mypage/reviewList";
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
    @GetMapping("mypage/delivery")
    public String delivery(Model model) {
        model.addAttribute("menu", "delivery");
        return "member/mypage/delivery";
    }

    /**
     * 마이페이지 - 개인정보수정
     */
    @GetMapping("mypage/info")
    public String info(Model model) {
        model.addAttribute("menu", "info");
        return "member/mypage/info";
    }
}
