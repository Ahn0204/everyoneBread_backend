package com.eob.common.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;
import com.eob.rider.model.data.ApprovalStatus;
import com.eob.rider.model.data.RiderEntity;
import com.eob.shop.model.data.ShopEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomDetailService customDetailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 1) 입력받은 아이디 / 비밀번호
        String loginId = authentication.getName();
        String rawPw = authentication.getCredentials().toString();

        // 2) UserDetailsService 호출 (조회만 함)
        CustomSecurityDetail userDetail = (CustomSecurityDetail) customDetailService.loadUserByUsername(loginId);

        MemberEntity member = userDetail.getMember();
        RiderEntity rider = userDetail.getRider();
        // ShopEntity shop = userDetail.getShop();
        System.out.println("authentication ");
        System.out.println(authentication);
        System.out.println("rawPw: " + rawPw);
        // 3) 비밀번호 검증 (가장 먼저!)
        if (!passwordEncoder.matches(rawPw, member.getMemberPw())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 틀렸습니다.");
        }

        // 4) ★ 비밀번호 맞으면 → 상태 검증
        switch (member.getStatus()) {
            case PENDING:
                if (member.getMemberRole() == MemberRoleStatus.RIDER) {
                    switch (rider.getAStatus()) {
                        case PENDING:
                            throw new DisabledException("가입 승인 대기중입니다.");
                        case UNDER_REVIEW:
                            throw new DisabledException("가입 서류 검토중입니다.");
                        case REVISION_REQUIRED:
                            throw new DisabledException("보완 요청중입니다.");
                    }
                }
                throw new DisabledException("가입 대기중입니다.");
            case SUSPENDED:
                throw new DisabledException("정지된 회원입니다.");
            case WITHDRAW:
                throw new DisabledException("탈퇴한 회원입니다.");
            case INACTIVE:
                throw new DisabledException("폐점 후 계정이 존재합니다.");
            case ACTIVE:
                break;
        }

        // // 5) 역할별 상세 검증
        // switch (member.getMemberRole()) {

        // case RIDER:
        // if (rider == null)
        // throw new DisabledException("라이더 정보가 존재하지 않습니다.");

        // switch (rider.getAStatus()) {
        // case PENDING:
        // throw new DisabledException("가입 승인 대기중입니다.");
        // case UNDER_REVIEW:
        // throw new DisabledException("가입 서류 검토중입니다.");
        // case REVISION_REQUIRED:
        // throw new DisabledException("보완 요청중입니다.");
        // case APPROVED:
        // break;
        // }
        // break;

        // case SHOP:
        // // if (shop == null)
        // // throw new DisabledException("가게 정보가 존재하지 않습니다.");
        // // break;

        // case ADMIN:
        // case USER:
        // break;
        // }

        // 6) 모두 통과 → 인증 성공 토큰 생성
        return new UsernamePasswordAuthenticationToken(
                userDetail,
                null,
                userDetail.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
