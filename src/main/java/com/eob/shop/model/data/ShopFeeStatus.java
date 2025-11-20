package com.eob.shop.model.data;

import lombok.Getter;

/**
 * 상점 매출 처리 타입 Enum
 *
 * - DEPOSIT  : 상점 입금
 * - WITHDRAW : 상점 환전(출금)
 *
 * DB에는 Enum의 name() 그대로 저장하는 것이 가장 안전함.
 */

@Getter
public enum ShopFeeStatus {

    DEPOSIT("상점 입금"),
    WITHDRAW("상점 환전");

    private final String description;

    ShopFeeStatus(String description) {
        this.description = description;
    }
}
