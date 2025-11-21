package com.eob.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eob.admin.model.data.InsertAdminForm;
import com.eob.admin.model.service.AdminService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin") // http://localhost:8080/admin으로 접속 시 매핑
@RequiredArgsConstructor
public class AdminController {

    public final AdminService adminService;

    // 시큐리티 사용 시 로그인 페이지로 매핑 예정?
    @GetMapping("/login")
    public String getAdminLoginP() {
        return "admin/common/admin-login";
    }

    // 관리자 로그인 처리
    @PostMapping("/login")
    public String adminLogin(@RequestParam(name = "id") String id, @RequestParam(name = "pw") String pw) {
        // 시큐리티 적용 후 아이디 비번 비교..로그인 처리

        return "admin/common/admin-main";
    }

    // // 로그인 성공 후 메인 페이지
    // @GetMapping("")
    // public String adminMain() {
    // return "/admin/common/admin-main";
    // }

    // 시큐리티로 처리?
    // 관리자 로그아웃 처리
    @GetMapping("/logout")
    public String adminLogout() { // @RequestParam String param
        // 세션 무효화처리.. 로그아웃..

        return "admin/common/admin-login";
    }

    // 관리자 계정 내역(추가) 페이지
    @GetMapping("/user/admin-list")
    public String getAdminList() {
        return "admin/user/admin-list";
    }

    // 관리자 계정 추가 처리
    @PostMapping("/insertAdmin")
    public String postMethodName(InsertAdminForm insertAdminForm, RedirectAttributes rttr) {

        // 계정 추가
        boolean insert = adminService.insertAdmin(insertAdminForm);

        // 성공 시 터미널에 계정이름 출력
        if (insert) {
            System.out.println("adminName:" + insertAdminForm.getName());
            rttr.addFlashAttribute("success");
        }
        // 일단 되는지 확인, 회원가입 로직완성되면 성공 시 실패시 알림 띄우기 여부 &리다이렉트 페이지 매핑 다시
        return "redirect:/admin/user/admin-list";
    }

}
