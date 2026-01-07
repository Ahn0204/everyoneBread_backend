package com.eob.admin.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eob.admin.model.data.BanInquiryEntity;
import com.eob.admin.model.data.InquiryEntity;

public interface BanInquiryRepository extends JpaRepository<BanInquiryEntity, Long> {

    // 관리자 - 모든 문의내역 조회
    Page<BanInquiryEntity> findAll(Pageable pageable);
}
