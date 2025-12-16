package com.eob.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 판매자 후기 관리 컨트롤러
 * - 화면 이동 전용
 * - Review 도메인 로직 사용 안 함
 */
@Controller
@RequestMapping("/shop/reviews")
public class ShopReviewController {

    @GetMapping
    public String reviewList() {
        return "shop/shop-reviews";
    }
}
