package com.eob.order.model.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eob.order.model.data.OrderHistoryEntity;
import com.eob.order.model.data.OrderStatus;
import com.eob.order.model.repository.OrderHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderHistoryRepository orderHistoryRepository;

    /**
     * [판매자]
     * 판매자 주문 관리 페이지에서 사용하는 주문 조회 메서드
     * - 기간 필터 화면에서만 적용
     * - ShopOrderController에서 사용
     */
    @Transactional(readOnly = true)
    public List<OrderHistoryEntity> findByShopNo(Long shopNo) {
        return orderHistoryRepository.findByShop_ShopNo(shopNo);
    }

    /**
     * [판매자]
     * 판매자 주문 관리 페이지에서 사용하는 상태별 주문 조회 메서드
     * - 상품 상태(status) 필터 화면
     * - 전체/대기중/배송중/배송완료/취소 등 상태별 조회
     * - ShopOrderController에서 사용
     */
    @Transactional(readOnly = true)
    public List<OrderHistoryEntity> findByShopNoAndStatus(Long shopNo, OrderStatus status) {
        return orderHistoryRepository.findByShop_ShopNoAndStatus(shopNo, status);
    }

    /**
     * [판매자]
     * 주문 수락 처리 메서드
     * - WAIT -> PREPARE 상태로 변경
     */
    @Transactional
    public void acceptOrder(Long orderNo) {
        // 주문 조회
        OrderHistoryEntity order = orderHistoryRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다.: "));

        // 현재 상태가 WAIT인 경우에만 PREPARE로 변경
        if (order.getStatus() != OrderStatus.WAIT) {
            throw new IllegalStateException("대기 상태의 주문만 수락할 수 있습니다.");
        }

        // 상태 변경
        order.setStatus(OrderStatus.PREPARE);
    }

    /**
     * [판매자]
     * 주문 거절 처리 메서드
     * - WAIT -> REJECT 상태로 변경
     * - 거절 사유 저장
     */
    @Transactional
    public void rejectOrder(Long orderNo, String reason) {

        // 1. 주문 조회
        OrderHistoryEntity order = orderHistoryRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 2. 상태 검증 (WAIT만 가능)
        if (order.getStatus() != OrderStatus.WAIT) {
            throw new IllegalStateException("대기 상태의 주문만 거절할 수 있습니다.");
        }

        // 3. 거절 처리
        order.setStatus(OrderStatus.REJECT);
        order.setRejectReason(reason);
    }

    /**
     * [판매자]
     * 오늘 주문 수 조회
     */
    @Transactional(readOnly = true)
    public long countTodayOrders(Long shopNo) {
        // 오늘 날짜의 시작과 끝 시간 계산
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        // 오늘 날짜의 끝 시간은 내일 00:00:00에서 1나노초 뺀 시간
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        // 조회
        return orderHistoryRepository.countTodayOrders(shopNo, start, end);
    }

    /**
     * [판매자]
     * 상태별 주문 수 조회
     */
    @Transactional(readOnly = true)
    public long countByStatus(Long shopNo, OrderStatus status) {
        return orderHistoryRepository.countByShop_ShopNoAndStatus(shopNo, status);
    }

    // 단일 주문 조회
    @Transactional(readOnly = true)
    public OrderHistoryEntity findById(Long orderNo) {
        return orderHistoryRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
    }



    
}