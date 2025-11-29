package com.eob.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class mainController {
    /*
     * return html페이지의 경로 => 맨 앞에 /가 안붙어야함
     * return redirect:/도메인(localhost:8080)뒤의 url => 맨 앞에 /가 붙어야함
     */

    //메인 페이지
    @GetMapping("")
    public String getMainP() {
        return "main/main";
    }

    //상점 목록
    @GetMapping("shopList")
    public String getShopList(@RequestParam String category) {
        return new String();
    }
    
    //고객센터
    @GetMapping("customerCenter")
    public String getCenterP() {
        return "redirect:/customerCenter/notice";
    }

    //공지페이지
    @GetMapping("/customerCenter/notice")
    public String getNoticeP() {
        return "customerCenter/notice";
    }
    
    
    
}
