package com.eob.order.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eob.order.model.data.OrderHistoryEntity;
import com.eob.order.model.data.OrderStatus;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, Long> {

    /**
     * [판매자]
     * - 판매자 주문 관리 페이지에서 사용
     * - 상점 기준 주문 전체 조회만 담당
     * @param shopNo 상점 고유 번호
     * @return 상점의 모든 주문 내역 리스트
     */
    List<OrderHistoryEntity> findByShop_ShopNo(Long shopNo);

    /**
     * [판매자]
     * - 판매자 주문 관리 페이지에서 사용
     * - 상태별 주문 조회 담당
     * @param shopNo 상점 고유 번호
     * @param status 주문 상태 코드
     * @return 상점의 특정 상태 주문 내역 리스트
     */
    List<OrderHistoryEntity> findByShop_ShopNoAndStatus(Long shopNo, OrderStatus status);



}