package com.eob.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eob.admin.model.data.InsertAdminForm;
import com.eob.admin.model.service.AdminService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/admin") // http://localhost:8080/admin으로 접속 시 매핑
@RequiredArgsConstructor
public class AdminController {

    public final AdminService adminService;

    // 로그인 페이지
    @GetMapping("/login")
    public String getAdminLoginP(HttpSession session, Model model) {

        // 세션 값 확인
        String error = (String) session.getAttribute("loginErrorMessage");

        // 로그인 실패 메세지가 있다면
        if (error != null) {
            // 에러 메시지 띄우기
            model.addAttribute("error", error);
            // 세션 삭제
            session.removeAttribute("loginErrorMessage");
        }

        return "admin/comm/admin-login";
    }

    // 메인 페이지
    @GetMapping("/")
    public String getAdminMain() {
        return "admin/comm/admin-main";
    }

    // 관리자 계정 내역(추가) 페이지
    @GetMapping("/user/admin-list")
    public String getAdminList(Model model) { // 그냥 insertAdminForm만 뷰로 전달해도, 뷰에서 th:object로 사용가능
        // redirect 시에는 flashAttribute에 insertAdminForm이 담아져옴(필드 에러 출력에 필요)

        // 필드 에러로 redirect되지 않은 새 페이지라면
        if (!model.containsAttribute("insertAdminForm")) {
            // insertAdminForm객체 생성
            model.addAttribute("insertAdminForm", new InsertAdminForm());
        }

        return "admin/user/admin-list";
    }

    // 관리자 계정 추가 처리
    @PostMapping("/user/insertAdmin")
    public String insertAdmin(@Valid InsertAdminForm insertAdminForm, BindingResult bindingResult,
            RedirectAttributes rttr) {

        // 입력값 유효성 검사
        // insertAdminForm에 담긴 값에 대한 유효성검사결과를 bindingResult객체로 사용
        if (bindingResult.hasErrors()) { // 오류가 있다면
            // 실패 알림 뜨게 하는 파라미터
            rttr.addFlashAttribute("isSucceeded", false);
            // 입력했던 값
            rttr.addFlashAttribute("insertAdminForm", insertAdminForm);
            // 필드 에러
            rttr.addFlashAttribute("org.springframework.validation.BindingResult.insertAdminForm", bindingResult);
            return "redirect:/admin/user/admin-list";
        }

        // 아이디 중복 여부 검사
        // !(중복id면false, 중복아니면 true) => 중복id면 if문 실행, 아니면 if문 스킵
        if (!adminService.isMemberIdAvailable(insertAdminForm.getAdminId())) {
            // 에러코드, 메세지
            // bindingResult.rejectValue(필드명, 에러코드, 에러메세지)
            bindingResult.rejectValue("adminId", "duplicateId", "이미 사용 중인 아이디입니다.");
            // 실패 알림 뜨게 하는 파라미터
            rttr.addFlashAttribute("isSucceeded", false);
            // 입력했던 값
            rttr.addFlashAttribute("insertAdminForm", insertAdminForm);
            // 필드 에러
            rttr.addFlashAttribute("org.springframework.validation.BindingResult.insertAdminForm", bindingResult);
            return "redirect:/admin/user/admin-list";
        }

        // 입력값에 오류가 없다면
        // 계정 추가
        boolean insert = adminService.insertAdmin(insertAdminForm);

        if (insert) {// 성공 시
            log.info("adminName:" + insertAdminForm.getAdminName()); // 터미널에 계정이름 출력
            rttr.addFlashAttribute("isSucceeded", true); // 뷰로 success(ture) 전달
        } else { // 실패시
            rttr.addFlashAttribute("isSucceeded", false);
        }

        return "redirect:/admin/user/admin-list"; // 새 admin-list 페이지가 다시 요청됨(redirect)
    }

    /*
     * return html페이지의 경로 => 맨 앞에 /가 안붙어야함
     * return redirect:도메인(localhost:8080)뒤의 url => 맨 앞에 /가 붙어야함
     */

    // ============== 정산 /admin/settlement
    // ==================================================

    // 헤더, 사이드바에서 '정산' 항목 클릭 시
    @GetMapping("/settlement")
    public String getSettlementP() {
        // 기본 뷰 = 정산 내역 페이지
        return "redirect:/admin/settlement/settlement-list";
    }

    // 정산내역 페이지
    @GetMapping("/settlement/settlement-list")
    public String getSettlementList() {
        return "admin/settlement/settlement-list";
    }

    // ============== 회원 /admin/user
    // ==================================================

    // 입점신청 내역 페이지
    @GetMapping("/user")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

}
