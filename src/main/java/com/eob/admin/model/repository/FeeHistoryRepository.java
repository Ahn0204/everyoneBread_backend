package com.eob.admin.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eob.admin.model.data.FeeHistoryEntity;

public interface FeeHistoryRepository extends JpaRepository<FeeHistoryEntity, Long> {

    // 가장 최신 엔티티 1개 조회
    FeeHistoryEntity findTopByOrderByCreatedAtDesc();
}
