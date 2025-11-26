package com.eob.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.service.ShopService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/*")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("")
    public String shopMain(){
        return "shop/shop-main";
    }

    // 상점 등록 폼 (회원가입 다음 버튼 누른 후 페이지)
    @GetMapping("shop-register")
    public String shopRegisterForm(){
        return "shop/shop-register";
    }

    @PostMapping("shop-register")
    public String shopRegister(ShopEntity shop){

        // 상점 정보 유효성 검사 예정

        // DB 저장
        shopService.saveShop(shop);

        // 저장 후 판매자 메인 페이지로 이동
        return "redirect:/shop/main";
    }
}
