package com.eob.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eob.member.model.data.MemberApprovalStatus;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;
import com.eob.member.model.data.WithdrawHistoryEntity;
import com.eob.member.model.dto.RegisterRequest;
import com.eob.member.repository.MemberRepository;
import com.eob.member.repository.WithdrawHistoryRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final WithdrawHistoryRepository withdrawHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    /*
       공통 체크 : 아이디 / 이메일 중복검사
    */
    private void validateDuplicate(RegisterRequest dto, BindingResult bindingResult) {

        if (!isMemberIdAvailable(dto.getMemberId())) {
            bindingResult.rejectValue("memberId", "duplicateId", "이미 사용 중인 아이디입니다.");
        }

        if (!isMemberEmailAvailable(dto.getMemberEmail())) {
            bindingResult.rejectValue("memberEmail", "duplicateEmail", "이미 사용 중인 이메일입니다.");
        }

        if (bindingResult.hasErrors()) return;

        if (!dto.getMemberPw().equals(dto.getMemberPwConfirm())) {
            bindingResult.rejectValue("memberPwConfirm", "pwMismatch", "비밀번호가 일치하지 않습니다.");
        }
    }

    /*
        공통 처리 : DTO → MemberEntity 변환
    */
    private MemberEntity toEntity(RegisterRequest dto) {

        MemberEntity entity = new MemberEntity();

        entity.setMemberId(dto.getMemberId());
        entity.setMemberPw(passwordEncoder.encode(dto.getMemberPw()));
        entity.setMemberName(dto.getMemberName());
        entity.setMemberEmail(dto.getMemberEmail());
        entity.setMemberPhone(dto.getMemberPhone());

        // 주소 조합
        String fullAddress = dto.getMemberAddress();
        if (dto.getMemberAddressDetail() != null && !dto.getMemberAddressDetail().isBlank()) {
            fullAddress += " " + dto.getMemberAddressDetail();
        }
        entity.setMemberAddress(fullAddress);

        // 주민등록번호
        entity.setMemberJumin(dto.getJumin1() + "-" + dto.getJumin2());

        return entity;
    }

    /*
        아이디 찾기
        - 휴대폰 번호 기준으로 회원 아이디 조회
        - 컨트롤러에서 목적(FIND_ID) 검증 후 호출됨
    */
    public String findMemberIdByPhone(String phone) {

        MemberEntity member = memberRepository
                .findByMemberPhone(phone)
                .orElseThrow(() ->
                        new IllegalArgumentException("회원 정보를 찾을 수 없습니다.")
                );

        return member.getMemberId();
    }

    /*
        아이디 찾기용 회원 존재 여부 체크
        - 이름 + 휴대폰 기준 회원 존재 여부 체크
    */
    public boolean existsByNameAndPhone(String name, String phone) {
        return memberRepository.existsByMemberPhoneAndMemberName(phone, name);
    }

    /*
        비밀번호 재설정
        - 인증 + RESET_PW 목적 검증은 Controller에서 수행
        - 실제 비밀번호 변경만 담당
    */
   @Transactional // 비밀번호 변경은 트랜잭션 보장 (중요)
    public void resetPasswordByPhone(String phone, String rawPassword) {
        MemberEntity member = memberRepository.findByMemberPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        member.changePassword(passwordEncoder.encode(rawPassword));
    }

    /*
        일반 회원 가입
    */
    public MemberEntity registerUser(RegisterRequest dto, BindingResult bindingResult, HttpSession session) {

        // 휴대폰 인증 여부 체크
        Boolean smsVerified = (Boolean) session.getAttribute("SMS_VERIFIED");
        if(smsVerified == null || !smsVerified){
            bindingResult.reject("sms.not.verified", "휴대폰 인증을 완료해주세요.");
            return null;
        }

        // 중복 및 PW 검사
        validateDuplicate(dto, bindingResult);
        if (bindingResult.hasErrors()) return null;

        // 엔티티 변환
        MemberEntity entity = toEntity(dto);

        // 역할 / 상태 지정
        entity.setMemberRole(MemberRoleStatus.USER);
        entity.setStatus(MemberApprovalStatus.ACTIVE);

        MemberEntity saved = memberRepository.save(entity);

        session.removeAttribute("SMS_VERIFIED");

        return saved;
    }

    /*
        판매자 회원 가입
    */
    public MemberEntity registerShop(RegisterRequest dto, BindingResult bindingResult) {

        // 중복 및 PW 검사
        validateDuplicate(dto, bindingResult);
        if (bindingResult.hasErrors()) return null;

        // 엔티티 변환
        MemberEntity entity = toEntity(dto);

        entity.setMemberRole(MemberRoleStatus.SHOP);
        // entity.setStatus(MemberApprovalStatus.PENDING); // 테스트 후 주석 제거
        entity.setStatus(MemberApprovalStatus.ACTIVE);  // 테스트용

        return memberRepository.save(entity);
    }

    /*
        라이더 회원 가입
    */
    // public MemberEntity registerRider(RegisterRequest dto, BindingResult bindingResult) {

    //     validateDuplicate(dto, bindingResult);
    //     if (bindingResult.hasErrors()) return null;

    //     MemberEntity entity = toEntity(dto);

    //     entity.setMemberRole(MemberRoleStatus.RIDER);
    //     entity.setStatus(MemberApprovalStatus.PENDING);

    //     return memberRepository.save(entity);
    // }


    // 아이디 중복 확인
    public boolean isMemberIdAvailable(String memberId){
        return !memberRepository.existsByMemberId(memberId);
    }

    // 이메일 중복 확인
    public boolean isMemberEmailAvailable(String memberEmail){
        return !memberRepository.existsByMemberEmail(memberEmail);
    }

    public MemberEntity findById(Long memberNo) {
        return memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
    }

    /**
     * 상점가입 2단계 전용 회원 생성 메서드
     * - BindingResult 사용하지 않음
     * - 1단계에서 이미 검증이 끝났기 때문에 중복검사 필요 없음
     */
    @Transactional
    public MemberEntity createShopMember(RegisterRequest dto) {

        // 중복검사 (선택) — 세션이 조작되는 경우 방지
        if (!isMemberIdAvailable(dto.getMemberId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (!isMemberEmailAvailable(dto.getMemberEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        MemberEntity entity = toEntity(dto);

        entity.setMemberRole(MemberRoleStatus.SHOP);
        entity.setStatus(MemberApprovalStatus.ACTIVE); // 테스트용
        // 실제 구동 시 해당 코드 주석 해제 후 위 코드 삭제
        // entity.setStatus(MemberApprovalStatus.PENDING);

        return memberRepository.save(entity);
    }

    /**
     * 회원 휴대폰 번호 수정
     */
    @Transactional
    public void updatePhone(Long memberNo, String phone) {
        MemberEntity member = memberRepository.findById(memberNo)
                .orElseThrow();
        member.setMemberPhone(phone);
    }

    /**
     * 회원 이메일 수정
     */
    @Transactional
    public void updateEmail(Long memberNo, String email) {
        MemberEntity member = memberRepository.findById(memberNo)
                .orElseThrow();
        member.setMemberEmail(email);
    }

    /**
     * 회원 비밀번호 확인
     */
    @Autowired
    public boolean checkPassword(MemberEntity member, String rawPw) {
        return passwordEncoder.matches(rawPw, member.getMemberPw());
    }

    /**
     * 회원 비밀번호 변경
     */
    @Transactional
    public void changePassword(Long memberNo, String newPw) {
        MemberEntity member = memberRepository.findById(memberNo)
                .orElseThrow();
        member.setMemberPw(passwordEncoder.encode(newPw));
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void withdrawMember(MemberEntity member, String reason) {

        // 1. 탈퇴 이력 저장
        WithdrawHistoryEntity history = new WithdrawHistoryEntity();
        history.setMember(member);
        history.setReason(reason);
        history.setRole(member.getMemberRole().name());

        withdrawHistoryRepository.save(history);

        // 2. 회원 상태 변경 (soft delete)
        member.setStatus(MemberApprovalStatus.WITHDRAW);

        // 3. 민감 정보 마스킹 (선택)
        member.setMemberPw("WITHDRAWN");
    }
}