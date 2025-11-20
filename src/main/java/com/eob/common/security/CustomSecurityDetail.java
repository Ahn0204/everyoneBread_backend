package com.eob.common.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.eob.member.model.data.MemberEntity;
import com.eob.rider.model.data.RiderEntity;
import com.eob.shop.model.data.ShopEntity;

/**
 * 로그인한 회원 정보를 스프링 시큐리티 세션에 저장하고 관리하는 담당 클래스
 */
public class CustomSecurityDetail implements UserDetails {

    // TODO: 회원 정보 객체를 해당 클래스에서 정의해줘야함

    // 실제 우리 시스템에서 사용하는 회원 정보 객체 (MemberDTO)
    private final MemberEntity member;

    // 생성자: 로그인한 사용자(MemberDTO)를 받아 세션에 저장
    public CustomSecurityDetail(MemberEntity member) {
        this.member = member;
    }

    // 세션에서 직접 MemberDTO를 꺼내 쓰고 싶을 때 사용
    public MemberEntity getMember() {
        return member;
    }

    // 💡 추가: role 에 따라 rider/shop 세부 정보 접근 가능
    public RiderEntity getRider() {
        return member.getRider();
    }

    // public ShopEntity getShop(){
    // return member.getShop();
    // }

    // ====================== UserDetails 필수 구현 메서드 ======================

    // 사용자 아이디 리턴
    // - Spring Security에서 username으로 인식
    // - 여기서는 memberId를 username으로 사용

    @Override
    public String getUsername() {
        return member.getMemberId();
    }

    // 사용자 비밀번호 리턴
    // - Spring Security에서 인증 시 사용하는 비밀번호
    // - 반드시 암호화(BCrypt 등)된 값이어야 함

    @Override
    public String getPassword() {
        return member.getMemberPw();
    }

    // 사용자 권한(Role) 리턴
    // - ROLE_USER, ROLE_ADMIN 등 권한 목록을 반환해야 함
    // - 현재는 null 반환 → 권한 체크가 필요한 경우 반드시 구현 필요
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + member.getMemberRole()));
    }

    // 계정 만료 여부
    // - true → 만료되지 않음 (항상 사용 가능)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부
    // - true → 잠기지 않음 (항상 로그인 가능)

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부
    // - true → 만료되지 않음 (항상 로그인 가능)

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부
     * - true → 활성화된 계정 (항상 로그인 가능)
     * member의 status 가 active 일 경우에만 로그인 가능
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
