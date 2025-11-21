package com.eob.shop.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.eob.shop.model.data.ShopEntity;
import com.eob.member.model.data.MemberEntity;
// import com.eob.order.model.data.OrderHistoryEntity;

@Entity
@Table(name = "SHOP_FEE")
@Getter
@Setter
public class ShopFeeEntity {

    /**
     * 상점 매출 고유 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_fee_seq")
    @SequenceGenerator(name = "shop_fee_seq", sequenceName = "SHOP_FEE_SEQ", allocationSize = 1)
    @Column(name = "SHOP_FEE_NO")
    private Long shopFeeNo;

    /**
     * 상점 FK
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_NO")
    private ShopEntity shop;

    /**
     * 주문 FK
     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ORDER_NO")
//    private OrderHistoryEntity order;

    /**
     * 회원 FK (입금/환전 요청자)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_NO")
    private MemberEntity member;

    /**
     * 상점 매출 타입 (상점 입금 / 상점 환전)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private ShopFeeStatus status;

    /**
     * 금액 (양수 고정)
     */
    @Column(name = "FEE_AMOUNT")
    private Long feeAmount;

    /**
     * 거래 후 잔액
     */
    @Column(name = "FEE_BALANCE")
    private Long feeBalance;

    /**
     * 등록 일시
     */
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}