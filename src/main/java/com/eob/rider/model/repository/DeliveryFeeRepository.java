package com.eob.rider.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eob.rider.model.data.DeliveryFeeEntity;

public interface DeliveryFeeRepository extends JpaRepository<DeliveryFeeEntity, Long> { // 예솔 작업

    // 상점별로 가장 최신 레코드 조회 -> 상점 엔티티 반환
    @Query("""
                select d
                from DeliveryFeeEntity d
                where d.createdAt = (
                    select max(d2.createdAt)
                    from DeliveryFeeEntity d2
                    where d2.riderNo = d.riderNo
                )
            """)
    Optional<List<DeliveryFeeEntity>> findLatestPerRider();

    // 잔액이 매개변수보다 같거나 큰 엔티티 조회
    // Optional<List<DeliveryFeeEntity>>
    // findByFeeAmountGreaterThanEqualOrderByCreateDateDesc(int feeAmount);
}
