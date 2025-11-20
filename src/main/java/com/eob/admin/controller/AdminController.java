package com.eob.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/admin/*")
public class AdminController {

    @GetMapping("login")
    public String adminLogin() {
        return "/admin/common/admin-login";
    }

    @PostMapping("login")
    public String adminLogin(@RequestParam(name = "id") String id, @RequestParam(name = "pw") String pw) {
        // 시큐리티 적용 후 아이디 비번 비교..로그인 처리

        return "/admin/";
    }

    @GetMapping("/")
    public String adminMain() {
        return "/admin/common/admin-main";
    }

}
