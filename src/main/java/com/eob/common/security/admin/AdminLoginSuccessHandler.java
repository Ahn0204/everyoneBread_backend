package com.eob.common.security.admin;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 로그인 성공시 행동 하는 핸들러
 * { 권한 별로 리다이렉트 페이지 다르게하기 }
 */
@Component
@RequiredArgsConstructor
public class AdminLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    // 필수 구현 메소드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        // 로그인한 유저의 권한을 꺼낸다(role값)
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        if (role.equals("ADMIN")) { // 꺼낸 권한이 ADMIN이라면
            response.sendRedirect("/"); // 로그인 성공 시 /admin으로 이동
        }
            // } else if (role.equals("RIDER")) {
        //     response.sendRedirect("/rider");
        // } else if (role.equals("SHOP")) {
        //     response.sendRedirect("/shop");
        // } else if (role.equals("USER")) {
        //     response.sendRedirect("/");
        // }

    }

}
