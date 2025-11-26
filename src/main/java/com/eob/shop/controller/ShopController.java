package com.eob.shop.controller;

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

    private final MemberService memberService; // 회원 저장용
    private final ShopService shopService;     // 상점 저장용


    /* ==========================================
       STEP 1 : 판매자 회원 기본정보 입력 화면
    ========================================== */
    @GetMapping("register/start")
    public String registerStartForm(Model model) {

        // RegisterRequest 객체를 미리 넣어야 Thymeleaf 에러 안 남
        model.addAttribute("registerRequest", new RegisterRequest());
        return "shop/shop-register-start";
    }


    /* ==========================================
       STEP 1 제출 → 회원 저장 후 STEP2 이동
    ========================================== */
    @PostMapping("register/start")
    public String registerStart(@Valid RegisterRequest dto,
                                BindingResult bindingResult,
                                Model model) {

        // 유효성 검증 실패 → 다시 폼으로
        if (bindingResult.hasErrors()) {
            return "shop/shop-register-start";
        }

        // role 강제 SHOP
        dto.setMemberRole("SHOP");

        // 회원 저장 (비밀번호 암호화 포함)
        MemberEntity newMember = memberService.registerShop(dto);

        // 다음 화면에서 필요하므로 memberNo 전달
        model.addAttribute("memberNo", newMember.getMemberNo());

        return "shop/shop-register-step";
    }


    /* ==========================================
       STEP 2 : 상점 정보 입력 화면
    ========================================== */
    @GetMapping("register/step")
    public String registerStepForm(@RequestParam("memberNo") Long memberNo, Model model) {

        model.addAttribute("memberNo", memberNo);
        return "shop/shop-register-step";
    }


    /* ==========================================
       STEP 2 제출 → 상점 정보 저장
    ========================================== */
    @PostMapping("register/step")
    public String registerStep(ShopEntity shop, @RequestParam("memberNo") Long memberNo) {

        // 회원 정보 조회
        MemberEntity member = memberService.findById(memberNo);

        // ShopEntity에 MemberEntity 세팅
        shop.setMember(member);

        shop.setSellerName(member.getMemberName());

        // shop 안에 memberNo 반드시 있어야 함
        shopService.saveShop(shop);

        // 저장 후 판매자 메인으로 이동
        return "redirect:/shop";
    }


    /* ==========================================
       판매자 메인
    ========================================== */
    @GetMapping("")
    public String shopMain() {
        return "shop/shop-main";
    }
}
