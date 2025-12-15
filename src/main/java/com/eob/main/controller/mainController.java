package com.eob.main.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eob.admin.model.data.CategoryEntity;
import com.eob.admin.model.repository.CategoryRepository;

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
    public ResponseEntity<?> getCategory() {
        // 리턴 객체 선언
        List<String> category = new ArrayList<>();

        // 카테고리 DB에서 대분류명 List 가져오기
        Optional<ArrayList<String>> _category = categoryRepository.findByDepth(0);

        // DB에 값이 존재하고 && Optional객체가 비어있지 않다면
        if (_category.isPresent() && !_category.get().isEmpty()) {
            // category에 Optional의 값 모두 add
            category.addAll(_category.get());
            System.out.println("카테고리 불러오기 실행:" + category);
            // 카테고리 List보내기
            return ResponseEntity.ok(category);
        } else {
            // Optional값이 없다면
            return ResponseEntity.status(500).body("카테고리가 존재하지 않습니다.");
        }

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
