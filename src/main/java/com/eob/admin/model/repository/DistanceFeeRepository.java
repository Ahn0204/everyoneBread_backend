package com.eob.admin.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eob.admin.model.data.DistanceFeeEntity;

public interface DistanceFeeRepository extends JpaRepository<DistanceFeeEntity, Long> {
    /**
     * ~거리에 해당하는 레코드 조회
     */
    Optional<DistanceFeeEntity> findByDistance(int distance);
}
