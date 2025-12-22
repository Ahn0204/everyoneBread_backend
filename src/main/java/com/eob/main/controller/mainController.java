package com.eob.main.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eob.admin.model.repository.CategoryRepository;
import com.eob.main.model.service.MainService;
import com.eob.shop.model.data.ProductEntity;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.service.ProductService;
import com.eob.shop.service.ShopService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class mainController {
    /*
     * return html페이지의 경로 => 맨 앞에 /가 안붙어야함
     * return redirect:/도메인(localhost:8080)뒤의 url => 맨 앞에 /가 붙어야함
     */

    public final CategoryRepository categoryRepository;
    public final MainService mainService;
    public final ProductService productService;
    public final ShopService shopService;

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
    public String getShopList(@RequestParam(name = "category") String category,
            @RequestParam(name = "page", defaultValue = "0") int page, Model model) {

        // pageable객체 생성
        // -> 일단 등록일자 최신순
        Pageable pageable = PageRequest.of(page, 8, Sort.by("createdAt").descending());

        // 상품에 카테고리 테이블 연결 시 삭제
        category = "BREAD";
        // 상점내역 조회, 페이징 객체로 리턴
        Page<ShopEntity> shopList = mainService.getShopList(category, pageable);

        if (shopList.getTotalElements() == 0) {
            // 지역, category에 해당하는 상점이 없을 경우
            model.addAttribute("noShopList", "주문 가능한 상점이 없습니다.");
        } else {
            // 지역, category에 해당하는 상점이 있을 경우
            model.addAttribute("shopList", shopList);
        }

        return "main/shopList";
    }

    /**
     * 위치 기반 상점 검색
     * 
     * @param location 객체
     * @return Page<ShopEntity>
     */
    // @PostMapping("getShopList")
    // @ResponseBody
    // public Page<ShopEntity> ajaxGetShopList(@RequestBody Map< entity) {
    // TODO: process POST request

    // return entity;
    // }

    @GetMapping("shopList/productList/{shopNo}")
    public String getProductList(@PathVariable(name = "shopNo") long shopNo, Model model) {

        // shopNo에 해당하는 shop 조회
        ShopEntity shop = shopService.findByShopNo(shopNo);
        model.addAttribute("shop", shop);

        // shopNo에 해당하는 productList 조회
        List<ProductEntity> productList = productService.getProductList(shopNo);
        model.addAttribute("productList", productList);
        return "main/productList";
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
