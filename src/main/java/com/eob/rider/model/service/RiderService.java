package com.eob.rider.model.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eob.member.model.data.MemberApprovalStatus;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;
import com.eob.member.repository.MemberRepository;
import com.eob.rider.model.data.MemberRegisterForm;
import com.eob.rider.model.data.RiderEntity;
import com.eob.rider.model.data.RiderEntity.RiderEntityBuilder;
import com.eob.rider.model.data.RiderRegisterForm;
import com.eob.rider.model.repository.RiderRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;
    private final RiderRepository riderRepository;

    @Transactional
    public void registerMember(MemberRegisterForm memberForm, RiderRegisterForm riderForm) {
        MemberEntity member = new MemberEntity();
        member.setMemberId(memberForm.getMemberId());
        member.setMemberPw(memberForm.getMemberPw());
        member.setMemberName(memberForm.getMemberName());
        member.setMemberJumin(memberForm.getMemberJuminFront() + "-" + memberForm.getMemberJuminBack());
        member.setMemberPhone(memberForm.getMemberPhone().replace("-", ""));
        member.setMemberEmail(memberForm.getMemberEmail());
        member.setMemberAddress(memberForm.getRoadAddress() + memberForm.getDetailAddress());
        member.setStatus(MemberApprovalStatus.PENDING);
        member.setCreatedAt(LocalDateTime.now());
        this.memberRepository.save(member);

        RiderEntity rider = RiderEntity.builder().member(member).riderLicense(riderForm.getDriverLicense())
                .licenseCreatedAt(riderForm.getLicenseCreatedAt()).createdAt(LocalDateTime.now()).build();

        this.riderRepository.save(rider);

    }

    public void passwordChange() {
        MemberEntity member = this.memberRepository.findByMemberId("test2");
        member.setMemberPw(passwordEncoder.encode("1234"));
        this.memberRepository.save(member);
    }

    // 아이디 중복확인 AJAX
    public boolean ajaxDuplicationId(String memberId) {
        return this.memberRepository.existsByMemberId(memberId);
    }

    // 이메일 중복확인 AJAX
    public boolean ajaxDuplicationEmail(String memberEmail) {
        return this.memberRepository.existsByMemberEmail(memberEmail);
    }

}
