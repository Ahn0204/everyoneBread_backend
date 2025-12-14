package com.eob.admin.model.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eob.admin.model.data.InsertAdminForm;
import com.eob.member.model.data.MemberApprovalStatus;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;
import com.eob.member.repository.MemberRepository;
import com.eob.rider.model.data.ApprovalStatus;
import com.eob.rider.model.data.RiderEntity;
import com.eob.rider.model.repository.RiderRepository;
import com.eob.shop.model.data.ShopApprovalStatus;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final RiderRepository riderRepository;
    private final ShopRepository shopRepository;

    public boolean insertAdmin(InsertAdminForm form) {

        // 관리자 계정 insert
        try {
            // 새 회원 정보 저장할 엔티티 생성
            MemberEntity member = new MemberEntity();
            member.setMemberId(form.getAdminId());
            member.setMemberPw(passwordEncoder.encode(form.getAdminPassword())); // 암호화
            member.setMemberName(form.getAdminName());
            // 이하 관리자 계정용 정보 대입
            member.setMemberJumin("000000-0000000");
            member.setMemberPhone("010-0000-0000");
            member.setMemberEmail("everyoneBread@gmail.com");
            member.setMemberAddress("서울시 관악구 남부순환로 1820 (봉천동) 6층 6B");
            member.setMemberRole(MemberRoleStatus.ADMIN); // 값이 ADMIN인 enum 대입
            member.setStatus(MemberApprovalStatus.ACTIVE); // 관리자계정은 승인 불필요

            // repository를 불러와서 DB에 insert
            memberRepository.save(member);

            return true;
        } catch (Exception e) { // insert중 문제 발생 시
            return false;
        }

    }

    // 아이디 중복 확인 메서드
    public boolean isMemberIdAvailable(String memberId) {
        // !(memberId가 이미 DB에 있으면 true 없으면 false) => 중복id면 false, 중복이 아니면 true
        return !memberRepository.existsByMemberId(memberId);
    }

    /**
     * 라이더 가입, 입점신청 승인
     */

    public boolean doApproval(String param, long objectNo) {
        try {
            if (param.equals("rider")) {
                // rider승인
                Optional<RiderEntity> _rider = riderRepository.findById(objectNo);
                RiderEntity rider = (RiderEntity) _rider.get();
                rider.setAStatus(ApprovalStatus.APPROVED);
                rider.setApprovedAt(LocalDateTime.now());
                this.riderRepository.save(rider);
                // member Status변경
                MemberEntity member = rider.getMember();
                member.setStatus(MemberApprovalStatus.ACTIVE);
                this.memberRepository.save(member);
            } else if (param.equals("shop")) {
                // shop승인
                Optional<ShopEntity> _shop = shopRepository.findById(objectNo);
                ShopEntity shop = (ShopEntity) _shop.get();
                shop.setStatus(ShopApprovalStatus.APPLY_APPROVED);
                shop.setApprovedDate(LocalDateTime.now());
                this.shopRepository.save(shop);
                // member Status변경
                MemberEntity member = shop.getMember();
                member.setStatus(MemberApprovalStatus.ACTIVE);
                this.memberRepository.save(member);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 라이더 가입, 입점신청 보완 요청
     */
    public boolean doRevision(String param, long objectNo, String rejectReason) {
        try {
            if (param.equals("rider")) {
                // rider보완 요청
                Optional<RiderEntity> _rider = riderRepository.findById(objectNo);
                RiderEntity rider = (RiderEntity) _rider.get();
                rider.setAStatus(ApprovalStatus.REVISION_REQUIRED);
                // 보완 사유 저장
                this.riderRepository.save(rider);
                // 유저에게 메일 보내기
            } else if (param.equals("shop")) {
                // shop보완 요청
                Optional<ShopEntity> _shop = shopRepository.findById(objectNo);
                ShopEntity shop = (ShopEntity) _shop.get();
                shop.setStatus(ShopApprovalStatus.APPLY_REJECT);
                // 보완 사유 저장
                this.shopRepository.save(shop);
                // 유저에게 메일 보내기
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
