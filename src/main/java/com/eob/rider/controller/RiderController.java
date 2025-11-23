package com.eob.rider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eob.rider.model.data.MemberRegisterForm;
import com.eob.rider.model.data.RiderRegisterForm;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderController {

    // 로그인 페이지 이동 메서드
    @GetMapping("/login")
    public String loginForm() {
        return "rider/rider-login";
    }

    // 회원가입 이동 메서드
    // Member 객체 정보만 입력 받는 페이지
    @GetMapping("/register/start")
    public String registerStart(MemberRegisterForm form) {
        return "rider/rider-register-start";
    }

    @PostMapping("/register/start")
    public String registerStart(@Valid MemberRegisterForm form, BindingResult bindingResult, HttpSession session) {
        if(bindingResult.hasErrors()) {
            return "rider/rider-register-start";
        }

        session.setAttribute("registerMember", form);
        session.setMaxInactiveInterval(10 * 60);

        return "rider/rider-register-step";
    }

    // Rider 객체 정보만 입력 받는 페이지
    @PostMapping("/register/step")
    public String registerStep(@Valid MemberRegisterForm memberForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "rider/rider-register-start";
        }

        // 입력받은 Member의 값을 registerMember로 session에 저장
        session.setAttribute("registerMember", memberForm);
        // session의 만료 시간을 10분으로 지정
        session.setMaxInactiveInterval(10 * 60);

        return "rider/rider-register-step";
    }

    // 회원가입 완료
    @PostMapping("/register/complete")
    public String registerStep2(@Valid RiderRegisterForm RiderRegisterForm, BindingResult bindingResult,
            HttpSession session) {
        MemberRegisterForm memberForm = (MemberRegisterForm) session.getAttribute("registerMember");
        // Member 정보가 session의 없을 경우
        if (memberForm == null) {
            return "rider/rider-register-start";
        }
        // 라이더의 정보가 제대로 입력되지 않았을 경우
        if (bindingResult.hasErrors()) {
            return "rider/rider-register-step";
        }
        // 회원 가입 로직 실행

        return "redirect:/rider/";
    }

}
