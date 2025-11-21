package com.eob.admin.model.service;

import org.springframework.stereotype.Service;

import com.eob.admin.model.data.InsertAdminForm;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;
import com.eob.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;

    public boolean insertAdmin(InsertAdminForm form) {

        // 아이디 중복 여부 확인 (아래 정의된 메서드 사용)
        if (!isMemberIdAvailable(form.getId())) { // !(중복id면false, 중복아니면 true) => 중복id면 if문 실행, 아니면 if문 스킵
            // bindingResult.rejectValue("memberId", "duplicateId", "이미 사용 중인 아이디입니다.");
            return false;
        }

        // 관리자 계정 insert
        try {
            // 새 회원 정보 저장할 엔티티 생성
            MemberEntity member = new MemberEntity();
            member.setMemberId(form.getId());
            member.setMemberPw(form.getPw());
            member.setMemberName(form.getName());
            // 이하 관리자 계정용 정보 대입
            member.setMemberJumin("000000-0000000");
            member.setMemberPhone("010-0000-0000");
            member.setMemberEmail("everyoneBread@gmail.com");
            member.setMemberAddress("서울시 관악구 남부순환로 1820 (봉천동) 6층 6B");
            member.setMemberRole(MemberRoleStatus.ADMIN); // 값이 ADMIN인 enum 대입

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
}
