package com.eob.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eob.admin.model.data.InsertAdminForm;
import com.eob.admin.model.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin") // http://localhost:8080/admin으로 접속 시 매핑
@RequiredArgsConstructor
public class AdminController {

    public final AdminService adminService;

    // 시큐리티 사용 시 로그인 페이지로 매핑 예정?
    @GetMapping("/login")
    public String getAdminLoginP() {
        return "admin/comm/admin-login";
    }

    // 관리자 로그인 처리
    @PostMapping("/login")
    public String adminLogin(@RequestParam(name = "id") String id, @RequestParam(name = "pw") String pw) {
        // 시큐리티 적용 후 아이디 비번 비교..로그인 처리

        return "admin/comm/admin-main";
    }

    // // 로그인 성공 후 메인 페이지
    // @GetMapping("")
    // public String adminMain() {
    // return "/admin/common/admin-main";
    // }

    // // 시큐리티로 처리?
    // // 관리자 로그아웃 처리
    // @GetMapping("/logout")
    // public String adminLogout() { // @RequestParam String param
    //     // 세션 무효화처리.. 로그아웃..

    //     return "admin/comm/admin-login";
    // }

    // 관리자 계정 내역(추가) 페이지
    @GetMapping("/user/admin-list")
    public String getAdminList(Model model) { //그냥 insertAdminForm만 뷰로 전달해도, 뷰에서 th:object로 사용가능
        //redirect 시에는 flashAttribute에 insertAdminForm이 담아져옴(필드 에러 출력에 필요)
        
        //필드 에러로 redirect되지 않은 새 페이지라면
        if(!model.containsAttribute("insertAdminForm")){
            //insertAdminForm객체 생성
            model.addAttribute("insertAdminForm", new InsertAdminForm());
        }

        return "admin/user/admin-list";
    }

    // 관리자 계정 추가 처리
    @PostMapping("/user/insertAdmin")
    public String postMethodName(@Valid InsertAdminForm insertAdminForm, BindingResult bindingResult, RedirectAttributes rttr) {

        // 입력값 유효성 검사
        // insertAdminForm에 담긴 값에 대한 유효성검사결과를 bindingResult객체로 사용
        if(bindingResult.hasErrors()){ //오류가 있다면
            //실패 알림 뜨게 하는 파라미터
            rttr.addFlashAttribute("isSucceeded",false); 
            //입력했던 값
            rttr.addFlashAttribute("insertAdminForm",insertAdminForm);
            //필드 에러
            rttr.addFlashAttribute("org.springframework.validation.BindingResult.insertAdminForm",bindingResult);
            return "redirect:/admin/user/admin-list";
        }

        // 입력값에 오류가 없다면
        // 계정 추가
        boolean insert = adminService.insertAdmin(insertAdminForm);

        if (insert) {// 성공 시 
            System.out.println("adminName:" + insertAdminForm.getAdminName()); //터미널에 계정이름 출력
            rttr.addFlashAttribute("isSucceeded",true); //뷰로 success(ture) 전달
        } else{ //실패시
            rttr.addFlashAttribute("isSucceeded",false);
        }

        return "redirect:/admin/user/admin-list"; //새 admin-list 페이지가 다시 요청됨(redirect)
    }

}
