package com.eob.main.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eob.admin.model.data.CategoryEntity;
import com.eob.admin.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class mainController {
    /*
     * return html페이지의 경로 => 맨 앞에 /가 안붙어야함
     * return redirect:/도메인(localhost:8080)뒤의 url => 맨 앞에 /가 붙어야함
     */

    public final CategoryRepository categoryRepository;

    // 메인 페이지
    @GetMapping("")
    public String getMainP() {
        return "main/main";
    }

    // 헤더 카테고리 항목 불러오기
    @GetMapping("getCategory")
    @ResponseBody
    public List<String> getCategory() {
        System.out.println("카테고리 불러오기 실행");

        List<String> category = new ArrayList<>();

        Optional<ArrayList<String>> optional = categoryRepository.findByDepth(0);

        if (optional.isPresent() && !optional.get().isEmpty()) {
            System.out.println(optional.get());
            category.addAll(optional.get());
        }

        System.out.println(category);
        return category;
    }

    // 상점 목록
    @GetMapping("shopList")
    public String getShopList(@RequestParam(name = "category") String category) {
        // 판매하는 상품의 category가 일치하는 상점 조회
        return "main/shopList";
    }

    // 고객센터
    @GetMapping("customerCenter")
    public String getCenterP() {
        return "redirect:/customerCenter/notice";
    }

    // 공지페이지
    @GetMapping("/customerCenter/notice")
    public String getNoticeP() {
        return "customerCenter/notice";
    }

}
