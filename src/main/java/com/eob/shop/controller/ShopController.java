package com.eob.shop.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.eob.member.model.dto.RegisterRequest;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.service.MemberService;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.service.ShopService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/*")
public class ShopController {

    private final MemberService memberService; 
    private final ShopService shopService;     

    /*
       판매자 로그인 페이지
    */
    @GetMapping("login")
    public String shopLogin() {
        return "shop/shop-login";
    }

    /*
        판매자 회원가입 페이지
    */
    @GetMapping("register/start")
    public String registerStartForm(Model model) {

        model.addAttribute("registerRequest", new RegisterRequest());
        return "shop/shop-register-start";
    }

    /*
        판매자 회원 저장
    */
    @PostMapping("register/start")
    public String registerStart(
            @Valid @ModelAttribute("registerRequest") RegisterRequest dto,
            BindingResult bindingResult,
            Model model) {

        // Valid 검증 실패
        if (bindingResult.hasErrors()) {
            return "shop/shop-register-start";
        }

        // ROLE 강제 SHOP 주입
        dto.setMemberRole("SHOP");

        // registerShop 호출 (이제 bindingResult 필요)
        MemberEntity newMember = memberService.registerShop(dto, bindingResult);

        // registerShop 내부 검증 실패
        if (bindingResult.hasErrors()) {
            return "shop/shop-register-start";
        }

        model.addAttribute("memberNo", newMember.getMemberNo());
        return "shop/shop-register-step";
    }

    /*
        상점 정보 입력 화면
    */
    @GetMapping("register/step")
    public String registerStepForm(@RequestParam("memberNo") Long memberNo, Model model) {

        model.addAttribute("memberNo", memberNo);
        return "shop/shop-register-step";
    }

    /*
        상점 정보 저장
    */
    @PostMapping("register/step")
    public String registerStep(
            ShopEntity shop,
            @RequestParam("memberNo") Long memberNo) {

        // 회원 조회
        MemberEntity member = memberService.findById(memberNo);

        // ShopEntity 세팅
        shop.setMember(member);
        shop.setSellerName(member.getMemberName());
        shop.setCreatedAt(LocalDateTime.now());

        shopService.saveShop(shop);

        return "redirect:/shop";
    }

    /*
        상점명 중복 확인
    */
    @GetMapping("check-name")
    @ResponseBody
    public boolean checkShopName(@RequestParam("shopName") String shopName) {
        return !shopService.existsByShopName(shopName);
    }

    /*
        판매자 메인 페이지
    */
    @GetMapping("")
    public String shopMain() {
        return "shop/shop-main";
    }
}
