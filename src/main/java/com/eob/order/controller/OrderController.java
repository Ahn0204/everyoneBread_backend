package com.eob.order.controller;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eob.order.model.data.OrderForm;
import com.eob.order.model.service.OrderService;
import com.eob.order.model.service.PortOneService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    /*
     * return html페이지의 경로 => 맨 앞에 /가 안붙어야함
     * return redirect:/도메인(localhost:8080)뒤의 url => 맨 앞에 /가 붙어야함
     */

    private final PortOneService portOneService;
    private final OrderService orderService;

    // 주문 페이지 접속
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/orderForm")
    public String getOrderForm(OrderForm orderForm) {
        return "order/orderForm";
    }

    // 결제 버튼 클릭 시 -> 결제 진행 후 DB insert
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/order")
    public String doOrder(OrderForm orderForm, Model model) {
        // if (bindingResult.hasErrors()) { // 유효성 검사에 에러가 있다면
        // return "/order/order";
        // }

        // 결제 검증 - 실제 가격과 결제된 가격이 일치하는지?
        // >>PortOneService.단건 조회 api호출
        // TotalPrice (form에서 넘어온 값) 과 realPrice(DB에서 꺼내온 값)
        // if) DB -> productNo 를 가지고 가격 가져와서 실제 상품의 가격과 일치하는지 검증
        // else) 가격이 일치하지 않을경우 결제 취소 , 사용자에게 잘못된 접근에 관해 경고 alert 띄워줌
        // >>결제 검증 방식 웹훅의 값을 사용하는 것으로 변경

        // 결제완료 후 주문 내역 CRUD
        // 토큰발급받기
        String token = portOneService.getToken();
        try {
            // 결제 성공 시 DB에 주문내역 insert
            this.orderService.insertOrder(orderForm);
            // 주문내역으로 이동
            model.addAttribute("title", "주문 완료");
            model.addAttribute("msg", "구매 내역 페이지로 이동합니다.");
            model.addAttribute("icon", "success");
            model.addAttribute("loc", "/member/mypage/orderList");
            model.addAttribute("removeCart", true);
            return "/comm/msg";
        } catch (Exception e) {
            e.printStackTrace();
            // DB작업 중 문제 발생 시
            portOneService.getRefund(token, orderForm.getMerchantUid()); // 결제 취소
            // 메인으로 이동
            model.addAttribute("title", "주문 실패");
            model.addAttribute("msg", "주문에 실패하여 메인으로 이동합니다.");
            model.addAttribute("icon", "error");
            model.addAttribute("loc", "/");
            return "/comm/msg";
        }

    }

}
