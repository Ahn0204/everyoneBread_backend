package com.eob.shop.model.data;

import lombok.Getter;

/**
 * 상품 상태 Enum
 *
 * 상태 설명:
 * - ON_SALE : 판매중
 * - SOLD_OUT : 품절
 * - DELETED : 삭제(비노출)
 *
 * DB에는 Enum의 name() 그대로 저장하는 것이 가장 안전함.
 */
@Getter
public enum ProductStatus {

    ON_SALE("판매중"),
    SOLD_OUT("품절"),
    DELETED("삭제");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }
}