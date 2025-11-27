package com.eob.member.repository;

import java.util.Optional;

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

    // 아이디 중복 확인
    // memberId가 이미 DB에 있으면 true 없으면 false.
    boolean existsByMemberId(String memberId);

    // 이메일 중복 확인
    boolean existsByMemberEmail(String memberEmail);

    MemberEntity findByMemberId(String memberId);

    // 라이더 아이디 중복 확인
    boolean existsByMemberIdAndMemberRole(String memberId, MemberRoleStatus rider);

    // 라이더 이메일 중복 확인
    boolean existsByMemberEmailAndMemberRole(String memberEmail, MemberRoleStatus rider);

}
