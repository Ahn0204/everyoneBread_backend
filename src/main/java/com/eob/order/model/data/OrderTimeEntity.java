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


}