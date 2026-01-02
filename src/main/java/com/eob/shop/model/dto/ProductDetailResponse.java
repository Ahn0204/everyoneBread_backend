package com.eob.shop.model.dto;

import java.time.LocalDateTime;

import com.eob.shop.model.data.ProductStatus;
import com.eob.shop.model.data.ProductEntity;

import lombok.Getter;
/**
 * 상품 상세 조회 전용 DTO
 * - Entity 직접 노출 금지
 * - AJAX 응답 안정성 확보용
 */
@Getter
public class ProductDetailResponse {

    private Long productNo;
    private String productName;
    private Long price;
    private String summary;
    private String catName;
    private ProductStatus status;
    private String imgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * ProductEntity → ProductDetailResponse 변환 생성자
     *
     * 📌 사용 목적
     * - Entity 직접 반환 시 발생하는 순환 참조(JPA 연관관계) 문제 방지
     * - API 응답에 필요한 필드만 선별하여 전달
     * - Controller에서 DTO 매핑 로직을 분리해 책임 명확화
     *
     * 📌 사용 위치
     * - 상품 상세 조회 API (/shop/products/{id}/detail)
     */
    public ProductDetailResponse(ProductEntity product) {
        this.productNo = product.getProductNo();
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.summary = product.getSummary();
        this.catName = product.getCatName();
        this.status = product.getStatus();
        this.imgUrl = product.getImgUrl();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }
}