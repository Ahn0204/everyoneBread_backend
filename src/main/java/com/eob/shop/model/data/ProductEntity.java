package com.eob.shop.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
public class ProductEntity {

    /**
     * 상품 고유 번호 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
    @Column(name = "PRODUCT_NO")
    private Long productNo;

    /**
     * 상점 고유 번호 FK
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_NO")
    private ShopEntity shop;

    /**
     * 카테고리명
     */
    @Column(name = "CAT_NAME", nullable = false, length = 50)
    private String catName;

    /**
     * 상품명
     */
    @Column(name = "PRODUCT_NAME", nullable = false, length = 100)
    private String productName;

    /**
     * 대표 이미지 경로
     */
    @Column(name = "IMG_URL", length = 255)
    private String imgUrl;

    /**
     * 상품 가격 (단위: 원)
     */
    @Column(name = "PRICE", nullable = false)
    private Long price;

    /**
     * 한줄 소개
     */
    @Column(name = "SUMMARY", length = 255)
    private String summary;

    /**
     * 주요 재료 (알러지 정보 등)
     */
    @Column(name = "INGREDIENT", length = 255)
    private String ingredient;

    /**
     * 상품 상태 
     * ON_SALE / SOLD_OUT / DELETED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private ProductStatus status;

    /**
     * 등록일
     */
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    /**
     * 수정일
     */
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ProductStatus.ON_SALE; // 기본값: 판매중
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}