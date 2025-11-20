package com.eob.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eob.member.model.data.MemberEntity;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    @Query("select m from MemberEntity m where m.memberId = :username")
    Optional<MemberEntity> findByLoginMember(@Param("username") String username);

    // 아이디 중복 확인
    boolean existsByMemberId(String memberId);

    // 이메일 중복 확인
    boolean existsByMemberEmail(String memberEmail);

}
