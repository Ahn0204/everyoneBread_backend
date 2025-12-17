package com.eob.order.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eob.order.model.data.OrderDetailEntity;

public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

    /**
     * 주문 번호로 주문 상세 내역 조회
     * @param orderNo
     */
    List<OrderDetailEntity> findByOrderNo_OrderNo(Long orderNo);
}
