package com.eob.admin.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eob.admin.model.data.DistanceFeeHistoryEntity;

public interface DistanceFeeHistoryRepository extends JpaRepository<DistanceFeeHistoryEntity, Long> {

    /**
     * 배송비 변경 이력 전체 조회
     * 페이징 객체 리턴
     */
    Page<DistanceFeeHistoryEntity> findAll(Pageable pageable);
}
