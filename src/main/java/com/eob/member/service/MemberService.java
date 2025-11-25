package com.eob.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eob.member.model.data.MemberApprovalStatus;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;
import com.eob.member.model.dto.RegisterRequest;
import com.eob.member.repository.MemberRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 처리 전체 로직
     */
    public void register(RegisterRequest dto, HttpSession session, BindingResult bindingResult) {

        /* 아이디 중복 */
        if (!isMemberIdAvailable(dto.getMemberId())) {
            bindingResult.rejectValue("memberId", "duplicateId", "이미 사용 중인 아이디입니다.");
            return;
        }

        /* 이메일 중복 */
        if (!isMemberEmailAvailable(dto.getMemberEmail())) {
            bindingResult.rejectValue("memberEmail", "duplicateEmail", "이미 사용 중인 이메일입니다.");
            return;
        }

        /* 비밀번호 일치 확인 */
        if (!dto.getMemberPw().equals(dto.getMemberPwConfirm())) {
            bindingResult.rejectValue("memberPwConfirm", "pwMismatch", "비밀번호가 일치하지 않습니다.");
            return;
        }

        // /* 휴대폰 인증 여부 */
        // if (!isPhoneVerified(dto.getMemberPhone(), session)) {
        //     bindingResult.rejectValue("memberPhone", "phoneNotVerified", "휴대폰 인증을 완료해주세요.");
        //     return;
        // }

        /* MemberEntity 생성 및 DTO → 엔티티 변환 */
        MemberEntity entity = new MemberEntity();

        entity.setMemberId(dto.getMemberId());

        // 비밀번호 암호화
        String encodedPw = passwordEncoder.encode(dto.getMemberPw());
        entity.setMemberPw(encodedPw);

        entity.setMemberName(dto.getMemberName());
        entity.setMemberEmail(dto.getMemberEmail());
        entity.setMemberPhone(dto.getMemberPhone());

        /* 주소 조합 (기본주소 + 상세주소) */
        String fullAddress = dto.getMemberAddress();
        if (dto.getMemberAddressDetail() != null && !dto.getMemberAddressDetail().isBlank()) {
            fullAddress += " " + dto.getMemberAddressDetail();
        }
        entity.setMemberAddress(fullAddress);


        // 주민등록번호 합치기
        entity.setMemberJumin(dto.getJumin1() + "-" + dto.getJumin2());

        // 역할 세팅
        /**
         * 회원 상태 설정
         * USER -> 로그인 시 메인 페이지로 이동 : ACTIVE
         * SHOP, RIDER -> 가입 대기 상태로 설정 : PENDING
         */
        MemberRoleStatus role = MemberRoleStatus.valueOf(dto.getMemberRole());
        entity.setMemberRole(role);

        switch(role){
            case USER:
                // 일반 회원은 바로 ACTIVE
                entity.setStatus(MemberApprovalStatus.ACTIVE);
                break;

            case SHOP:
            case RIDER:
                // 상점/라이더는 가입 대기 상태로 설정
                entity.setStatus(MemberApprovalStatus.PENDING);
                break;

            default:
                entity.setStatus(MemberApprovalStatus.PENDING);
                break;
        }

        /* 저장 */
        memberRepository.save(entity);

        /* 인증 세션 제거 */
        // cleanupPhoneAuth(dto.getMemberPhone(), session);
    }

    // 아이디 중복 확인
    public boolean isMemberIdAvailable(String memberId){
        return !memberRepository.existsByMemberId(memberId);
    }

    // 이메일 중복 확인
    public boolean isMemberEmailAvailable(String memberEmail){
        return !memberRepository.existsByMemberEmail(memberEmail);
    }

    // // 휴대폰 인증 여부 확인
    // public boolean isPhoneVerified(String phone, HttpSession session) {
    //     String verified = (String) session.getAttribute("AUTH_OK_" + phone);
    //     return "OK".equals(verified);
    // }

    // // 휴대폰 인증 세션 정리
    // private void cleanupPhoneAuth(String phone, HttpSession session) {
    //     session.removeAttribute("AUTH_CODE_" + phone);
    //     session.removeAttribute("AUTH_EXPIRE_" + phone);
    //     session.removeAttribute("AUTH_OK_" + phone);
    // }
}
