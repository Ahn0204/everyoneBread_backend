package com.eob.common.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.eob.member.model.data.MemberEntity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 로그인 성공시 행동 하는 핸들러
 */
@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomSecurityDetail principal = (CustomSecurityDetail) authentication.getPrincipal();
        MemberEntity member = principal.getMember();
        // * - USER : 일반 소비자 (회원 기능)
        // * - SHOP : 판매자(상점) - 상품 등록/관리, 주문 처리 가능
        // * - RIDER : 배달 기사 - 배달/수령 상태 처리
        // * - ADMIN : 관리자 - 전체 회원/상점/상품 관리, 승인/반려 처리
        switch (member.getMemberRole()) {
            case USER:
                response.sendRedirect("/");
                break;
            case SHOP:
                response.sendRedirect("/shop/");
                break;
            case RIDER:
                response.sendRedirect("/rider/");
                break;
            case ADMIN:
                response.sendRedirect("/admin/");
                break;
        }
    }

}
