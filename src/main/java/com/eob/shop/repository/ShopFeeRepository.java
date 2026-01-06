package com.eob.shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eob.shop.model.data.ShopFeeEntity;

public interface ShopFeeRepository extends JpaRepository<ShopFeeEntity, Long> { // 예솔 작업

    // 상점별로 가장 최신 레코드 조회 -> 상점 엔티티 반환
    @Query("""
                select f
                from ShopFeeEntity f
                where f.createdAt = (
                    select max(f2.createdAt)
                    from ShopFeeEntity f2
                    where f2.shop = f.shop
                )
            """)
    Optional<List<ShopFeeEntity>> findLatestPerShop();

    // 최신 잔액이 매개변수보다 같거나 큰 엔티티 조회
    // Optional<List<ShopFeeEntity>>
    // findTopByFeeAmountGreaterThanEqualOrderByCreateDateDesc(int feeAmount);
}
