package com.eob.shop.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.eob.shop.model.data.ShopEntity;

@Entity
@Table(name = "SHOP_DAILY_STATS")
@Getter
@Setter
public class ShopDailyStatsEntity {

    /**
     * 통계 고유 번호 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_stats_seq")
    @SequenceGenerator(name = "shop_stats_seq", sequenceName = "SHOP_STATS_SEQ", allocationSize = 1)
    @Column(name = "STAT_NO")
    private Long statNo;

    /**
     * 상점 고유 번호 FK
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_NO")
    private ShopEntity shop;

    /**
     * 통계 기준 일자
     */
    @Column(name = "STAT_DATE", nullable = false)
    private LocalDateTime statDate;

    /**
     * 주문 건수
     */
    @Column(name = "ORDER_CNT")
    private Integer orderCnt;

    /**
     * 총 매출 합계 (단위: 원)
     */
    @Column(name = "SALES_TOTAL")
    private Long salesTotal;

    /**
     * 판매자 상품 수
     */
    @Column(name = "PRODUCT_CNT")
    private Integer productCnt;

    /**
     * 품절 상품 수
     */
    @Column(name = "SOLDOUT_CNT")
    private Integer soldoutCnt;

    /**
     * 리뷰 개수
     */
    @Column(name = "REVIEW_CNT")
    private Integer reviewCnt;

    /**
     * 평균 별점
     */
    @Column(name = "AVG_RATING")
    private Double avgRating;

    /**
     * 통계 생성 시각
     */
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        // 기본값 세팅
        if (this.orderCnt == null) this.orderCnt = 0;
        if (this.salesTotal == null) this.salesTotal = 0L;
        if (this.productCnt == null) this.productCnt = 0;
        if (this.soldoutCnt == null) this.soldoutCnt = 0;
        if (this.reviewCnt == null) this.reviewCnt = 0;
        if (this.avgRating == null) this.avgRating = 0.0;
    }
}
