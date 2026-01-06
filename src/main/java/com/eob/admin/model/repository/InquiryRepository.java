package com.eob.admin.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.eob.admin.model.data.InquiryEntity;

public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {

    // 관리자 - 모든 문의내역 조회
    Page<InquiryEntity> findAll(Pageable pageable);

    // 사용자 - 일반 문의 - 내 문의 내역 조회
    Page<InquiryEntity> findByMember_MemberNo(@Param("memberNo") long memberNo, Pageable pageable);

}
