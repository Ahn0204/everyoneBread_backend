package com.eob.order.model.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "OrderDetail")
public class OrderDetailEntity {

    /**
     * 주문 상세 고유번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_detail_seq")
    @SequenceGenerator(name = "order_detail_seq", sequenceName = "order_detail_seq", allocationSize = 1)
    @Column(name = "ORDER_DETAIL_NO")
    private Long orderDetailNo;

    /**
     * 주문 번호
     */
    @ManyToOne
    @JoinColumn(name = "order_no", nullable = false)
    private OrderHistoryEntity orderNo;
}