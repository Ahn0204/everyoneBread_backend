package com.eob.comm.security;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eob.member.model.data.MemberApprovalStatus;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.repository.MemberRepository;
import com.eob.rider.model.data.RiderEntity;
import com.eob.rider.model.repository.RiderRepository;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 정보 조회
 */
@Service
@RequiredArgsConstructor
public class CustomDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    private final RiderRepository riderRepository;

    private final ShopRepository shopRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. 회원 조회
        MemberEntity member = memberRepository.findByLoginMember(username)
                .orElseThrow(() -> new UsernameNotFoundException("아이디 또는 비밀번호가 틀렸습니다."));

        // PENDING=가입 대기 | ACTIVE=사용가능 | SUSPENDED=정지 | WITHDRAW=탈퇴 | INACTIVE=폐점 후 계정
        // 존재

        switch (member.getStatus()) {
            case PENDING:
                throw new DisabledException("가입 대기중입니다.");
            case SUSPENDED:
                // 정지 일자 조회 로직
                throw new DisabledException("정지된 회원입니다. ");
            case WITHDRAW:
                throw new DisabledException("탈퇴한 회원입니다.");
            case INACTIVE:
                throw new DisabledException("폐점 후 계정이 존재합니다.");
            case ACTIVE:
                break;
        }

        switch (member.getMemberRole()) {
            case RIDER:
                RiderEntity rider = riderRepository.findByMember(member)
                        .orElseThrow(() -> new DisabledException("라이더 정보가 없습니다."));
                switch (rider.getAStatus()) {
                    case PENDING:
                        throw new DisabledException("가입 승인 대기중 입니다.");
                    case UNDER_REVIEW:
                        throw new DisabledException("가입 서류 검토중 입니다.");
                    case REVISION_REQUIRED:
                        throw new DisabledException("보완 요청중 입니다.");
                    case APPROVED:
                        break;
                }
                break;

            case SHOP:
                ShopEntity shop = shopRepository.loginShop(member)
                        .orElseThrow(() -> new DisabledException("가게 정보가 없습니다."));
                break;
            case ADMIN:
                break;
            case USER:
                break;

            default:
                break;
        }
        return new CustomSecurityDetail(member);
    }

}
