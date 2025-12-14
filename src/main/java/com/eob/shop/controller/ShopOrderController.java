package com.eob.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 판매자 주문 관리 컨트롤러
 *
 * - 판매자 센터에서 주문 목록을 조회하는 화면 담당
 * - Order 도메인의 비즈니스 로직은 사용하지 않음
 * - 화면 이동용 컨트롤러
 */
@Controller
@RequestMapping("/shop/orders")
public class ShopOrderController {

    /**
     * 주문 관리 목록 페이지
     * URL: GET /shop/orders
     */
    @GetMapping
    public String orderList() {
        return "shop/shop-orders";
    }
}
