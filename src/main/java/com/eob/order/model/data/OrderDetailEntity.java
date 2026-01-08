package com.eob.order.model.data;

import java.time.LocalDateTime;

import com.eob.shop.model.data.ProductEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.ToString;

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
    @ToString.Exclude
    private OrderHistoryEntity orderNo;

    // 여기 아래부터 성진 추가 12/17 00:24 AM
    /**
     * 상품 번호
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_no", nullable = false)
    private ProductEntity productNo;

    /**
     * 주문 수량
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * 상품 가격
     */
    @Column(name = "price", nullable = false)
    private int price;

    /**
     * 입력된 일시
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Transient
    private ProductEntity product;
}