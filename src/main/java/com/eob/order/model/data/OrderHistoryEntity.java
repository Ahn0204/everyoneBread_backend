package com.eob.order.model.data;

import java.time.LocalDateTime;
import java.util.List;

import com.eob.member.model.data.MemberEntity;
import com.eob.shop.model.data.ShopEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Order_History")
public class OrderHistoryEntity {

    /**
     * 주문 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_SEQ", allocationSize = 1)
    @Column(name = "ORDER_NO")
    private Long orderNo;

    /**
     * 상점 객체
     */
    @ManyToOne
    @JoinColumn(name = "shop_no", nullable = false)
    private ShopEntity shop;

    /**
     * 구매자 member객체
     */
    @ManyToOne
    @JoinColumn(name = "buyer_member_no", nullable = false)
    private MemberEntity member;

    /**
     * 상품 상세 내역
     */
    @OneToMany(mappedBy = "orderNo", fetch = FetchType.LAZY) // mappedBy에는 클래스명이 아니라, 자식 엔티티의 필드명을 써야 합니다.
    @JoinColumn(name = "order_detail_no", nullable = false)
    private List<OrderDetailEntity> orderDetail;

    /**
     * 배송지
     */
    @Column(nullable = false, length = 4000)
    private String orderAddress;

    /**
     * 수령인 연락처
     */
    @Column(nullable = false, length = 100)
    private String orderPhone;

    /**
     * 라이더 요청사항
     */
    @Column(nullable = true, length = 4000)
    private String riderRequest;

    /**
     * 요청사항
     */
    @Column(nullable = true, length = 4000)
    private String orderRequest;

    /**
     * 배송비
     */
    @Column(nullable = false)
    private int deliveryFee;

    /**
     * 상품 총액
     */
    @Column(nullable = false)
    private int productPrice;

    /**
     * 총 결제 금액 (상품 총액 + 배송비)
     */
    @Column(nullable = false)
    private int orderPrice;

    /**
     * 주문 진행 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private OrderStatus status;

    /**
     * 거절 사유
     */
    @Column(nullable = true, length = 4000)
    private String rejectReason;

    /**
     * 배달 기사
     */
    @ManyToOne
    @JoinColumn(name = "rider_member_no", nullable = true)
    private MemberEntity rider;

    /**
     * 주문 시간
     */
    @OneToOne
    @JoinColumn(name = "order_time_no", nullable = true)
    private OrderTimeEntity orderTime;
    /**
     * 배송 완료 사진
     */
    @Column(nullable = true, length = 4000)
    private String filepath;
    /**
     * 정기 배송 주기
     */
    @Column(nullable = true)
    private LocalDateTime deliveryCycle;

    /**
     * 다음 배송일
     */
    @Column(nullable = true)
    private LocalDateTime nextDelivery;
    /**
     * 빌링키 (정기 결제 시 필요)
     */
    @Column(nullable = true)
    private String customerUid;

    /**
     * 다음 결제일
     */
    @Column(nullable = true)
    private LocalDateTime nextPaymentAt;

    /**
     * 정기 결제 전 알림 여부
     */
    @Column(nullable = true, length = 100)
    private String payAlert;
}