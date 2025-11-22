package com.eob.comm.security.rider;
// package com.eob.common.security.rider;

// import org.springframework.security.authentication.DisabledException;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import
// org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import com.eob.member.model.data.MemberApprovalStatus;
// import com.eob.member.model.data.MemberEntity;
// import com.eob.rider.model.data.RiderEntity;
// import com.eob.rider.model.repository.RiderRepository;

// import lombok.RequiredArgsConstructor;

// /**
// * 사용자 정보 조회
// */
// @Service
// @RequiredArgsConstructor
// public class RiderDetailService implements UserDetailsService {

// private final RiderRepository riderRepository;

// @Override
// public UserDetails loadUserByUsername(String username) throws
// UsernameNotFoundException{

// // 1. 회원 조회
// MemberEntity member = memberRepository.findByLoginId(username)
// .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));

// // 2. 공통 Member 상태 검사 (탈퇴/정지 등)
// if (member.getStatus() != MemberApprovalStatus.ACTIVE) {
// throw new DisabledException("현재 상태에서는 로그인이 불가능합니다.");
// }

// // 3. 역할별 승인 조건 검사
// switch (member.getMemberRole()) {

// case ROLE_RIDER -> {
// RiderEntity rider = riderRepository.findByMember(member)
// .orElseThrow(() -> new DisabledException("라이더 정보가 없습니다."));

// if (rider.getStatus() != RiderStatus.ACTIVE) {
// throw new DisabledException("관리자 승인 후 로그인할 수 있습니다.");
// }
// }

// case ROLE_SHOP -> {
// ShopInfo shop = shopInfoRepository.findByMember(member)
// .orElseThrow(() -> new DisabledException("매장 정보가 없습니다."));

// if (shop.getStatus() != ShopStatus.ACTIVE) {
// throw new DisabledException("관리자 승인 후 로그인할 수 있습니다.");
// }
// }

// case ROLE_ADMIN -> {
// // 필요하면 추가 검증 가능
// // 관리자는 대부분 ACTIVE 고정
// }

// default -> {
// // 기본 구매자는 MemberStatus ACTIVE 만되면 됨
// }
// }

// // 4. UserDetails 반환
// return new CustomUserDetails(member);
// }
// }
