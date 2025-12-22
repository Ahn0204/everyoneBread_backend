package com.eob.order.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "OrderTime")
public class OrderTimeEntity {

    /**
     * 주문 시간 고유번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_time_seq")
    @SequenceGenerator(name = "order_time_seq", sequenceName = "order_time_seq", allocationSize = 1)
    @Column(name = "ORDER_TIME_NO")
    private Long orderTimeNo;

    // 여기 아래로 성진 추가함 12/16 23:40 PM
    /**
     * 주문 (OrderHistory)
     */
    @OneToOne
    @JoinColumn(name = "ORDER_ORDER_NO")
    private OrderHistoryEntity order;

    /**
     * 고객 주문 시간
     */
    @Column(name = "ORDERED_AT")
    private LocalDateTime orderedAt;

    /**
     * 상점 주문 거절 시간
     */
    @Column(name = "REJECTED_AT")
    private LocalDateTime rejectedAt;

    /**
     * 상점 배달 요청 시간
     */
    @Column(name = "REQUESTED_AT")
    private LocalDateTime requestedAt;

    /**
     * 라이더 배정 시간
     */
    @Column(name = "ASSIGNED_AT")
    private LocalDateTime assignedAt;

    /**
     * 픽업 완료 시간
     */
    @Column(name = "PICKUP_AT")
    private LocalDateTime pickupAt;

    /**
     * 배송 완료 시간
     */
    @Column(name = "COMPLETED_AT")
    private LocalDateTime completedAt;

    /**
     * 고객 주문 취소 시간
     */
    @Column(name = "CANCELED_AT")
    private LocalDateTime canceledAt;

    /**
     * 취소 사유
     */
    @Column(name = "CANCEL_REASON")
    private LocalDateTime cancelReason;

    /**
     * 픽업 도착 예정 시간
     */
    @Column(name = "EST_PICKUP_AT")
    private LocalDateTime estPickupAt;

    /**
     * 배달 완료 예정 시간
     */
    @Column(name = "EST_DELIVERY_AT")
    private LocalDateTime estDeliveryAt;
}