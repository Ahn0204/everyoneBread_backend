package com.eob.member.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

        // 로그인 회원 조회
        @Query("select m from MemberEntity m where m.memberId = :username")
        Optional<MemberEntity> findByLoginMember(@Param("username") String username);

        // 회원 아이디 중복 확인
        // memberId가 이미 DB에 있으면 true 없으면 false.
        boolean existsByMemberId(String memberId);

        // 회원 이메일 중복 확인
        boolean existsByMemberEmail(String memberEmail);

        // 라이더 아이디 중복 확인
        boolean existsByMemberIdAndMemberRole(String memberId, MemberRoleStatus rider);

        // 라이더 이메일 중복 확인
        boolean existsByMemberEmailAndMemberRole(String memberEmail, MemberRoleStatus rider);

        // 라이더
        MemberEntity findByMemberId(String memberId);

        // 라이더 아이디 찾기(memberName,memberEmail)
        Optional<MemberEntity> findByMemberNameAndMemberEmailAndMemberRole(String memberName, String memberEmail,
                        MemberRoleStatus rider);

        // 라이더 아이디 찾기(memberName,memberPhone)
        Optional<MemberEntity> findByMemberNameAndMemberPhoneAndMemberRole(String memberName, String memberPhone,
                        MemberRoleStatus rider);

        /**
         * 관리자 계정 조회 - 페이징 객체 리턴
         * 
         * @param pageable
         * @return Page<MemberEntity>
         */
        @Query("select m from MemberEntity m where m.memberRole='ADMIN'")
        Page<MemberEntity> findByMemberRoleAdmin(Pageable pageable);
}