package com.eob.member.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.eob.common.security.CustomSecurityDetail;
import com.eob.member.model.data.WishlistStatus;
import com.eob.member.model.dto.WishlistToggleRequest;
import com.eob.member.service.WishlistService;

import lombok.RequiredArgsConstructor;

/**
 * 즐겨찾기(찜) API 전용 Controller
 *
 * HTML 반환 X
 * JSON 응답 O
 * JS(fetch/AJAX)에서 호출
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * 즐겨찾기(찜) 토글 API
     *
     * URL    : POST /wishlist/toggle
     * 요청값 : shopNo (RequestBody)
     * 처리   :
     *   - 없으면 → INSERT (ACTIVE)
     *   - ACTIVE → DELETED
     *   - DELETED → ACTIVE
     * 응답값 : 변경된 상태 (ACTIVE / DELETED)
     */
    @PostMapping("/toggle")
    public Map<String, String> toggleWishlist(
            @RequestBody WishlistToggleRequest request
    ) {

        /* 1. SecurityContext에서 로그인 사용자 정보 조회 */
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomSecurityDetail user =
                (CustomSecurityDetail) authentication.getPrincipal();

        /* 2️. 로그인 회원 번호 추출 */
        Long memberNo = user.getMember().getMemberNo();

        /* 3️. WishlistService 토글 로직 호출 */
        WishlistStatus resultStatus =
                wishlistService.toggleWishlist(memberNo, request.getShopNo());

        /* 4. 결과 상태를 JSON으로 반환 */
        return Map.of(
                "status", resultStatus.name()
        );
    }
}