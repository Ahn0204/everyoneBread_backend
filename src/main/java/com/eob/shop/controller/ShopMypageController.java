package com.eob.shop.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eob.common.security.CustomSecurityDetail;
import com.eob.shop.model.data.ShopApprovalStatus;
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
    public String shopMypage() {
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
    public Map<String, String> updateShopInfo(@PathVariable(name = "type") String type,
            @RequestBody Map<String, String> body, @AuthenticationPrincipal CustomSecurityDetail principal) {
        // 상점 본인 확인
        Long memberNo = principal.getMember().getMemberNo();
        
        if ("saveAll".equals(type)) {
            shopService.updateShopInfo(memberNo, body);
        } else {
            shopService.updateShopInfo(memberNo, type, body.get("value"));
        }
        return Map.of("result", "OK");
    }

    /**
     * 개인 정보 페이지
     */
    @GetMapping("/info")
    public String shopInfo(@AuthenticationPrincipal CustomSecurityDetail principal, Model model) {
        Long memberNo = principal.getMember().getMemberNo();
        ShopEntity shop = shopService.findByMemberNo(memberNo);

        model.addAttribute("member", principal.getMember());
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

    /**
     * 폐점 신청 처리
     * URL : /shop/mypage/apply/close
     */
    @PostMapping("/close/request")
    @ResponseBody
    @Transactional
    public Map<String, String> requestClose(
            @AuthenticationPrincipal CustomSecurityDetail principal,
            @RequestBody Map<String, String> body) {
        Long memberNo = principal.getMember().getMemberNo();
        String reason = body.get("reason");

        ShopEntity shop = shopService.findByMemberNo(memberNo);

        // 이미 폐점 관련 상태면 차단
        if (shop.getStatus() == ShopApprovalStatus.CLOSE_REVIEW ||
                shop.getStatus() == ShopApprovalStatus.CLOSE_APPROVED) {
            return Map.of("result", "FAIL", "message", "이미 폐점 처리 중입니다.");
        }

        shop.setStatus(ShopApprovalStatus.CLOSE_REVIEW);
        shop.setClosedRequestAt(LocalDateTime.now());
        shop.setClosedReason(reason);

        return Map.of("result", "OK");
    }

    /**
     * 정산 관리 페이지
     * URL : /shop/mypage/settlement
     */
    @GetMapping("/settlement")
    public String shopSettlement(
            @AuthenticationPrincipal CustomSecurityDetail principal,
            Model model) {
        Long memberNo = principal.getMember().getMemberNo();

        // 1. 상점 조회
        ShopEntity shop = shopService.findByMemberNo(memberNo);

        // 2. 정산 요약 금액 계산
        // ※ 아직 정산 엔티티가 없으므로 Order 기준으로 계산
        long totalSales = shopService.calculateTotalSales(shop.getShopNo());
        long settledAmount = shopService.calculateSettledAmount(shop.getShopNo());
        long expectedAmount = totalSales - settledAmount;

        // 3. 모델 전달
        model.addAttribute("shop", shop);
        model.addAttribute("menu", "settlement");

        model.addAttribute("totalSales", totalSales);
        model.addAttribute("settledAmount", settledAmount);
        model.addAttribute("expectedAmount", expectedAmount);

        // 4. 정산 내역 (추후 SettlementEntity로 교체)
        model.addAttribute("settlementList", List.of());

        return "shop/mypage/shop-settlement";
    }
}