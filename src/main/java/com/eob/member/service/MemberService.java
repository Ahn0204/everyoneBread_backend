package com.eob.member.service;

import org.springframework.stereotype.Service;

import com.eob.member.model.data.MemberEntity;
import com.eob.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원가입 후 회원 정보 저장
    public void saveMember(MemberEntity member){
        // 암호화는 시큐리티 붙이고 적용
        memberRepository.save(member);
    }

    // 아이디 중복 확인
    public boolean isMemberIdAvailable(String memberId){
        return !memberRepository.existsByMemberId(memberId);
    }

    // 이메일 중복 확인
    public boolean isMemberEmailAvailable(String memberEmail){
        return !memberRepository.existsByMemberEmail(memberEmail);
    }

}
