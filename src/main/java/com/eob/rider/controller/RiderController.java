package com.eob.rider.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.eob.common.security.CustomDetailService;
import com.eob.common.security.CustomSecurityDetail;
import com.eob.common.util.FileUtil;
import com.eob.common.util.CustomFileException;
import com.eob.common.util.StringUtil;
import com.eob.member.model.data.MemberEntity;
import com.eob.order.model.data.OrderHistoryEntity;
import com.eob.order.model.data.OrderStatus;
import com.eob.rider.model.data.MemberRegisterForm;
import com.eob.rider.model.data.RiderEntity;
import com.eob.rider.model.data.RiderRegisterForm;
import com.eob.rider.model.service.RiderService;

import jakarta.mail.Multipart;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;
    private final StringUtil stringUtil;

    // 메인 페이지 이동 메서드
    @GetMapping("/")
    public String mainPage(Model model, @AuthenticationPrincipal CustomSecurityDetail principal) {
        List<OrderHistoryEntity> list = this.riderService.getOrderHistory("all", principal.getMember());
        System.out.println(list.size());
        model.addAttribute("list", list);

        return "rider/rider-main";
    }

    // 주문 관련 페이지 이동
    @GetMapping("/order/{type}")
    public String orderPage(@PathVariable(name = "type") String type,
            @AuthenticationPrincipal CustomSecurityDetail principal, Model model) {
        List<OrderHistoryEntity> list = this.riderService.getOrderHistory(type, principal.getMember());
        model.addAttribute("list", list);

        if (type.equals("request") || type.equals("myOrder")) {
            // 라이더의 주문 목록과 내 주문 목록 요청일 경우
            return "rider/rider-main";
        } else {
            // 그 외 모든 잘못된 요청
            return "redirect:/rider/";
        }
    }

    @GetMapping("/order/refresh/{type}")
    public String orderRefreshPate(
            @PathVariable(name = "type") String type,
            @AuthenticationPrincipal CustomSecurityDetail principal, Model model) {
        List<OrderHistoryEntity> list = this.riderService.getOrderHistory(type, principal.getMember());
        model.addAttribute("list", list);

        if (type.equals("request") || type.equals("myOrder") || type.equals("all")) {
            // 라이더의 주문 목록과 내 주문 목록 요청일 경우
            return " rider/fragment_order :: orderList";
        } else {
            // 그 외 모든 잘못된 요청
            return "redirect:/rider/";
        }
    }

    // 내정보 페이지 이동 메서드
    @GetMapping("/myInfo")
    public String myInfoPage(@AuthenticationPrincipal CustomSecurityDetail principal, Model model) {
        StringUtil util = new StringUtil();
        MemberEntity member = util.maskMemberEntity(principal.getMember());
        model.addAttribute("maskInfo", member);
        return "rider/rider-myInfo";
    }

    // 로그인 페이지 이동 메서드
    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        // session에 저장된 loginErrorMessage 가져오기
        String errorMsg = (String) session.getAttribute("loginErrorMessage");
        Long riderNo = (Long) session.getAttribute("riderNo");
        // 저장된 errorMsg가 있을 경우 Model을 통해 페이지로 값 전송 및 해당 에러 메세지 지우기 (flashAttribute처럼 행동)
        if (errorMsg != null) {
            model.addAttribute("errorMsg", errorMsg);
            model.addAttribute("riderNo", riderNo);
            session.removeAttribute("loginErrorMessage");
        }

        return "rider/rider-login";
    }

    // 라이더 서류 정보 수정 페이지 이동 메서드
    @GetMapping("/revision-request/{param}")
    public String revisionRequest(@PathVariable(name = "param") long param, HttpSession session, Model model,
            RiderRegisterForm riderRegisterForm) {
        // session에 저장된 riderNo 가져오기
        Long riderNo = (Long) session.getAttribute("riderNo");
        // riderNo가 session에 없거나 해당 접근 권한이 없을때
        if (riderNo == null || riderNo == 0 || param != riderNo) {
            model.addAttribute("icon", "error");
            model.addAttribute("title", "접근 차단");
            model.addAttribute("msg", "잘못된 접근입니다.");
            model.addAttribute("loc", "/rider/login");
            return "comm/msg";
        }

        RiderEntity rider = this.riderService.getRider(riderNo);
        riderRegisterForm.setDriverLicense(rider.getRiderLicense());
        riderRegisterForm.setLicenseCreatedAt(rider.getLicenseCreatedAt());
        model.addAttribute("rider", rider);

        return "rider/rider-revision";
    }

    // 라이더 서류 정보 저장 메서드
    @PostMapping("/revision-request")
    public String revisionRequest(@Valid RiderRegisterForm riderRegisterForm, BindingResult bindingResult,
            HttpSession session, Model model) {
        System.out.println("rider/revision-request 실행");
        // TODO: process POST request
        Long riderNo = (Long) session.getAttribute("riderNo");
        if (bindingResult.hasErrors()) {
            return "rider/rider-revision";
        }
        try {
            this.riderService.updateRevisionRequest(riderRegisterForm, riderNo);
        } catch (CustomFileException e) {
            bindingResult.rejectValue("licenseFile", "empty", e.getMessage());
            return "rider/rider-revision";
        } catch (Exception e) {
            return "rider/rider-revision";
        }

        session.removeAttribute("riderNo");
        model.addAttribute("icon", "success");
        model.addAttribute("title", "제출 성공");
        model.addAttribute("msg", "서류가 정상적으로 제출되었습니다.");
        model.addAttribute("loc", "/rider/login");
        return "comm/msg";
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
        // @RequestParam("file") MultipartFile file,
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.hasErrors());
            System.out.println(bindingResult.getAllErrors());
            return "redirect:/rider/register/step";
        }

        MemberRegisterForm memberForm = (MemberRegisterForm) session.getAttribute("registerMember");

        try {
            // 회원/라이더 정보 저장
            this.riderService.registerMember(memberForm, riderRegisterForm);

            return "redirect:/rider/register/complete";
        } catch (CustomFileException e) {
            bindingResult.rejectValue("licenseFile", "empty", e.getMessage());
            return "rider/rider-register-step";
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
    @GetMapping("/register/complete")
    public String registerStep2(HttpSession session, Model model) {

        session.removeAttribute("registerMember");
        model.addAttribute("title", "회원가입 성공");
        model.addAttribute("msg", "회원가입에 성공했습니다.");
        model.addAttribute("icon", "success");
        model.addAttribute("loc", "/rider/login");
        return "comm/msg";
    }

    // =======================================================================================================
    // 아이디/비밀번호 찾기 로직
    @PostMapping("/ajaxFindById")
    @ResponseBody
    public HashMap<String, String> ajaxFindById(@RequestParam("memberName") String memberName,
            @RequestParam(value = "memberEmail", required = false) String memberEmail,
            @RequestParam(value = "memberPhone", required = false) String memberPhone) {

        HashMap<String, String> result = new HashMap<String, String>();
        // 두개의 값 모두 서버로 오지 않았을 경우 해당 오류 메세지 출력
        if (memberEmail == null && memberPhone == null) {
            result.put("result", "false");
            result.put("msg", "parameter missing");
            return result;
        }
        System.out.println("memberPhone : " + memberPhone);
        System.out.println("memberEmail : " + memberEmail);

        result = this.riderService.ajaxFindById(memberName, memberEmail, memberPhone);

        return result;
    }

    @GetMapping("/modalTest")
    public String modalTest() {
        return "rider/modalTest";
    }

    @GetMapping("/passwordChange")
    public String passwordChange() {
        this.riderService.passwordChange();
        return "redirect:/rider/login";
    }

    // ================================================================
    // order 관련 로직
    // OrderNo로 OrderHistory 조회
    @PostMapping("/ajaxOrderDetail")
    @ResponseBody
    public OrderHistoryEntity ajaxOrderDetail(@RequestParam("orderNo") Long orderNo,
            @AuthenticationPrincipal CustomSecurityDetail principal) {

        OrderHistoryEntity dto = this.riderService.ajaxOrderDetail(orderNo);
        MemberEntity rider = dto.getRider();
        MemberEntity member = principal.getMember();
        OrderStatus status = dto.getStatus();
        System.out.println(rider);
        System.out.println(member);
        System.out.println(status);
        System.out.println(dto);
        // order 상태가 REQUEST 가 아닐때 즉 ASSIGN, PICKUP, COMPLETE 일때
        if (!status.equals(OrderStatus.REQUEST)) {
            if (rider.getMemberNo() != member.getMemberNo()) {
                dto.setOrderAddress(stringUtil.maskAddress(dto.getOrderAddress()));
                dto.setOrderPhone(stringUtil.maskPhone(dto.getOrderPhone()));
            } else {
                dto.setOrderPhone(stringUtil.formatPhone(dto.getOrderPhone()));
            }
        } else {
            dto.setOrderPhone(stringUtil.formatPhone(dto.getOrderPhone()));
        }

        return dto;
    }
}
