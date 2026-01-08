package com.eob.shop.controller;

import com.eob.common.security.CustomSecurityDetail;
import com.eob.member.model.data.MemberEntity;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.service.ShopService;
import com.eob.order.model.data.OrderHistoryEntity;
import com.eob.order.model.data.OrderStatus;
import com.eob.order.model.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 판매자 주문 관리 컨트롤러
 * - 판매자 전용 주문 목록 조회
 * - 조회만 담당 (저장/수정 X)
 * 
 * - 주문 수락 / 거절 처리
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/orders")
public class ShopOrderController {

    private final OrderService orderService;
    private final ShopService shopService;

    /**
     * 주문 관리 페이지
     * URL: GET /shop/orders
     */
    @GetMapping
    public String orderList(
            @RequestParam(required = false) OrderStatus status, // 주문 상태 필터
            @RequestParam(required = false) String startDate, // 시작 날짜 필터
            @RequestParam(required = false) String endDate, // 종료 날짜 필터
            @AuthenticationPrincipal CustomSecurityDetail principal,
            Model model) {

        MemberEntity member = principal.getMember(); // 현재 로그인한 회원 정보 조회
        ShopEntity shop = shopService.findByMemberNo(member.getMemberNo()); // 상점 정보 조회

        // 상점 기준 주문 전체 조회
        List<OrderHistoryEntity> orders;

        if (status == null) {
            // 전체 + 기간 필터
            orders = orderService.findByShopNo(shop.getShopNo());
        } else {
            // 상태 + 기간 필터
            orders = orderService.findByShopNoAndStatus(shop.getShopNo(), status);
        }

        // 4. 화면 전달 (기간은 화면 상태 유지용)
        model.addAttribute("orders", orders);
        model.addAttribute("status", status);
        model.addAttribute("orderStatuses", OrderStatus.values());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "shop/shop-orders";
    }

    /**
     * 주문 수락
     * - WAIT -> PREPARE
     */
    @PostMapping("/{orderNo}/accept")
    public String acceptOrder(@PathVariable Long orderNo, @RequestParam(required = false) OrderStatus status) {
        orderService.acceptOrder(orderNo);
        return "redirect:/shop/orders" + (status != null ? "?status=" + status.name() : "");
    }

    /**
     * 주문 거절
     * - WAIT -> REJECT
     */
    @PostMapping("/{orderNo}/reject")
    public String rejectOrder(@PathVariable Long orderNo, @RequestParam String reason,
            @RequestParam(required = false) OrderStatus status) {
        orderService.rejectOrder(orderNo, reason);
        return "redirect:/shop/orders" + (status != null ? "?status=" + status.name() : "");
    }

    /**
     * 주문관리 대시보드 숫자 (AJAX)
     * URL: GET /shop/orders/dashboard
     */
    @GetMapping("/dashboard")
    @ResponseBody
    public Map<String, Long> orderDashboard(@AuthenticationPrincipal CustomSecurityDetail principal) {
        // 1. 현재 로그인한 회원의 상점 정보 조회
        MemberEntity member = principal.getMember();
        // 2. 상점 번호로 오늘 주문 수, 상태별 주문 수 조회
        ShopEntity shop = shopService.findByMemberNo(member.getMemberNo());
        // 3. 결과 맵으로 반환
        Long shopNo = shop.getShopNo();

        // 4. 결과 맵 생성
        Map<String, Long> result = new HashMap<>();
        result.put("today", orderService.countTodayOrders(shopNo)); // 오늘 주문 수
        result.put("wait", orderService.countByStatus(shopNo, OrderStatus.ORDER)); // 대기 주문 수
        result.put("delivering", orderService.countByStatus(shopNo, OrderStatus.PICKUP)); // 배송중 주문 수
        result.put("complete", orderService.countByStatus(shopNo, OrderStatus.COMPLETE)); // 완료 주문 수

        return result;
    }

    /**
     * 주문 상세 페이지
     * URL: GET /shop/orders/{orderNo}
     */
    @GetMapping("/{orderNo}")
    public String orderDetail(
            @PathVariable Long orderNo,
            Model model) {
        OrderHistoryEntity order = orderService.findById(orderNo);
        model.addAttribute("order", order);
        return "shop/shop-order-detail";
    }

}
