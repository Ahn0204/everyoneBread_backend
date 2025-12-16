package com.eob.order.model.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

}