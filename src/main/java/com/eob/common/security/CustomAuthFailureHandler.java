package com.eob.common.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 로그인 처리 실패시 행동하는 핸들러
 */
@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        System.out.println("💥 로그인 실패 이유: " + exception.getClass().getSimpleName());
        System.out.println("💥 상세 메시지: " + exception.getMessage());

        // 1. session에 메시지 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginErrorMessage", exception.getMessage());
        // a. 만약 CustomSecurityException을 통해 예외가 발생했을 경우 세션에 라이더 번호와 멤버 번호를 세션에 담음
        if (exception instanceof CustomSecurityException ex) {
            session.setAttribute("riderNo", ex.getRiderNo());
        }

        // 2. 로그인한 위치(URI) 가져오기
        String loginURI = request.getRequestURI();
        String redirectURL;

        // 에러 메시지 URL 인코딩
        String errorMsg = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);

        // 3. URI에 따른 URL 분기
        if (loginURI.contains("/rider/login")) {
            redirectURL = "/rider/login";
        } else if (loginURI.contains("/shop/login")) {
            redirectURL = "/shop/login?error=" + errorMsg;
        } else if (loginURI.contains("/admin/login")) {
            redirectURL = "/admin/login";
        } else {
            // 기본 로그인 페이지
            redirectURL = "/member/login";
        }

        // 4. 해당 URL로 이동
        response.sendRedirect(redirectURL);

        // 중요!
        // 1. loginURI 를 redirect 시키면 안됌
        // getRequestURI()로 가져온 것은 예를 들어 POST /rider/login 이기 때문에 GET으로 변환해서 redirect
        // 시켜야함
        // 2. 해당 로그인 Controller에서 session에 loginErrorMessage 체크 후 있으면 model로 전송후 session
        // 지우기

    }

}
