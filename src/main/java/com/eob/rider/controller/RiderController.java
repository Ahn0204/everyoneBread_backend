package com.eob.rider.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eob.member.model.data.MemberEntity;
import com.eob.rider.model.data.MemberRegisterForm;
import com.eob.rider.model.data.RiderEntity;
import com.eob.rider.model.data.RiderRegisterForm;
import com.eob.rider.model.service.RiderService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;

    // 로그인 페이지 이동 메서드
    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        String errorMsg = (String) session.getAttribute("loginErrorMessage");

        if (errorMsg != null) {
            model.addAttribute("errorMsg", errorMsg);
            session.removeAttribute("loginErrorMessage");
        }

        return "rider/rider-login";
    }

    // 회원가입 이동 메서드
    // 1. Member 객체 정보만 입력 받는 페이지
    @GetMapping("/register/start")
    public String registerStart(MemberRegisterForm form) {
        return "rider/rider-register-start";
    }

    // 회원가입 Member 객체 정보 저장 및 라이더 정보 입력 페이지 이동
    // 2. Member 객체 정보 저장 (Session) 및 라이더 정보 입력 페이지 이동
    @PostMapping("/register/start")
    public String registerStart(@Valid MemberRegisterForm form, BindingResult bindingResult, HttpSession session,
            Model model) {
        if (bindingResult.hasErrors()) {
            String[] fields = { "memberId", "memberPw", "memberPwRe", "memberName", "memberJuminFront",
                    "memberJuminBack", "memberEmail", "memberPhone", "roadAddress", "addressDetail" };
            for (String field : fields) {
                if (bindingResult.getFieldErrorCount(field) > 0) {

                    // 해당 필드의 첫 번째 메시지
                    String msg = bindingResult.getFieldError(field).getDefaultMessage();

                    // 화면에 메시지 전달 (Flash or model)
                    System.out.println("에러 발생 필드 = " + field + " / 메시지 = " + msg);

                    break; // ★ 첫 번째 에러만 처리하고 종료
                }
            }

            System.out.println(bindingResult);
            return "rider/rider-register-start";
        }

        session.setAttribute("registerMember", form); // key : registerMember 로 객체 저장
        session.setMaxInactiveInterval(10 * 60); // Session 만료 시간 10분으로 설정

        model.addAttribute("riderRegisterForm", new RiderRegisterForm());
        return "rider/rider-register-step";
    }

    @GetMapping("/register/step")
    public String registerStep(RiderRegisterForm riderRegisterForm, HttpSession session) {
        MemberEntity member = (MemberEntity) session.getAttribute("registerMember");
        if (member == null) {
            return "redirect:rider/register/step";
        }
        return "rider/rider-register-step";
    }

    // Rider 객체 정보만 입력 받는 페이지
    @PostMapping("/register/step")
    public String registerStep(@Valid RiderRegisterForm riderRegisterForm, BindingResult bindingResult,
            HttpSession session) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.hasErrors());
            System.out.println(bindingResult.getAllErrors());
            return "redirect:/rider/register/start";
        }

        MemberRegisterForm memberForm = (MemberRegisterForm) session.getAttribute("registerMember");

        try {
            this.riderService.registerMember(memberForm, riderRegisterForm);

            return "redirect:/register/complete";
        } catch (Exception e) {
            return "rider/rider-register-step";

        }

    }

    // 아이디 중복확인 AJAX
    @PostMapping("/ajaxDuplicationId")
    @ResponseBody
    public boolean ajaxDuplicationId(@RequestParam("memberId") String memberId) {
        boolean result = this.riderService.ajaxDuplicationId(memberId);
        return result;
    }

    // 이메일 중복확인 AJAX
    @PostMapping("/ajaxDuplicationEmail")
    @ResponseBody
    public boolean postMethodName(@RequestParam("memberEmail") String memberEmail) {
        boolean result = this.riderService.ajaxDuplicationEmail(memberEmail);
        return result;
    }

    // 회원가입 완료
    @PostMapping("/register/complete")
    public String registerStep2(HttpSession session) {

        session.removeAttribute("registerMember");

        return "redirect:/rider/";
    }

    @GetMapping("/")
    public String mainPage() {
        return "rider/rider-main";
    }

    @GetMapping("/passwordChange")
    public String passwordChange() {
        this.riderService.passwordChange();
        return "redirect:/rider/login";
    }

}
