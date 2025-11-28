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
     * return redirect:도메인(localhost:8080)뒤의 url => 맨 앞에 /가 붙어야함
     */

    @GetMapping("")
    public String getMain() {
        return "main/main";
    }

}
