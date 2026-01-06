package com.eob.shop.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eob.common.security.CustomSecurityDetail;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.service.ShopService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/mypage")
public class ShopMypageController {

    private final ShopService shopService;

    // 기본 마이페이지 진입
    @GetMapping("")
    public String shopMypage(){
        return "redirect:/shop/mypage/manage";
    }

    /**
     * 상점 관리 페이지
     * URL : /shop/mypage/manage
     */
    @GetMapping("/manage")
    public String shopManage(@AuthenticationPrincipal CustomSecurityDetail principal, Model model) {
        Long memberNo = principal.getMember().getMemberNo();
        ShopEntity shop = shopService.findByMemberNo(memberNo);

        model.addAttribute("shop", shop);
        model.addAttribute("menu", "manage");
        model.addAttribute("openStatus", shopService.getShopOpenStatus(shop));

        return "shop/mypage/shop-manage";
    }

    /**
     * 상점 정보 수정
     * URL : /shop/mypage/update/{type}
     * type : shopName, shopDesc, shopContact
     */
    @PostMapping("/update/{type}")
    @ResponseBody
    public Map<String, String> updateShopInfo(
            @PathVariable String type,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomSecurityDetail principal
    ) {
        Long memberNo = principal.getMember().getMemberNo();
        shopService.updateShopInfo(memberNo, type, body.get("value"));
        return Map.of("result", "OK");
    }


    /**
     *  개인 정보 페이지
     */
    @GetMapping("/info")
    public String shopInfo(@AuthenticationPrincipal CustomSecurityDetail principal, Model model) {
        Long memberNo = principal.getMember().getMemberNo();
        ShopEntity shop = shopService.findByMemberNo(memberNo);

        model.addAttribute("shop", shop);
        model.addAttribute("menu", "info");

        return "shop/mypage/shop-info";
    }

    /**
     * 폐점 신청 페이지
     * URL : /shop/mypage/apply
     */
    @GetMapping("/apply")
    public String shopApply(@AuthenticationPrincipal CustomSecurityDetail principal, Model model) {
        Long memberNo = principal.getMember().getMemberNo();
        ShopEntity shop = shopService.findByMemberNo(memberNo);

        model.addAttribute("shop", shop);
        model.addAttribute("menu", "apply");

        return "shop/mypage/shop-apply";
    }
}