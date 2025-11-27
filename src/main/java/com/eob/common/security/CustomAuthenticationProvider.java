package com.eob.common.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.eob.member.model.data.MemberApprovalStatus;
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
        ShopEntity shop = userDetail.getShop();
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
        switch (member.getMemberRole()) {

            case RIDER:
                if (rider == null)
                    throw new DisabledException("라이더 정보가 존재하지 않습니다.");

                switch (rider.getAStatus()) {
                    case PENDING:
                        throw new DisabledException("가입 승인 대기중입니다.");
                    case UNDER_REVIEW:
                        throw new DisabledException("가입 서류 검토중입니다.");
                    case REVISION_REQUIRED:
                        throw new DisabledException("보완 요청중입니다.");
                    case APPROVED:
                        break;
                }
                break;

            case SHOP:
                if (shop == null){
                    throw new DisabledException("가게 정보가 존재하지 않습니다.");
                }
                // 로그인한 사용자가 이 상점의 주인인지 확인
                if(!shop.getMember().getMemberNo().equals(member.getMemberNo())){
                    throw new DisabledException("상점 정보와 회원 정보가 일치하지 않습니다.");
                }

                switch(shop.getStatus()){
                    case APPLY_REVIEW:
                        throw new DisabledException("입점 심사 중입니다.");
                    case APPLY_REJECT:
                        throw new DisabledException("입점 심사가 반려되었습니다.");
                    case CLOSE_REVIEW:
                        throw new DisabledException("폐점 검토 중입니다.");
                    case CLOSE_REJECT:
                        break;  // 폐점 반려 -> 영업은 가능함
                    case APPLY_APPROVED:
                        break;  // 입점 승인 완료 -> 상점 로그인 가능
                    case CLOSE_APPROVED:
                        throw new DisabledException("폐점된 상점입니다.");
                }
                break;


            // case ADMIN:


            case USER:
                switch(member.getStatus()){
                    case ACTIVE:
                        break;
                    case INACTIVE:
                        throw new DisabledException("휴면 계정입니다.");
                    case WITHDRAW:
                        throw new DisabledException("탈퇴한 계정입니다.");
                    case SUSPENDED:
                        throw new DisabledException("정지된 계정입니다.");
                    case PENDING:
                        throw new DisabledException("가입 절차가 완료되지 않았습니다.");
                }
        }

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
