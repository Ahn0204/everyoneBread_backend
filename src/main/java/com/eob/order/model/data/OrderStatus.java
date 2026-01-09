package com.eob.order.model.data;

import lombok.Getter;

/**
 * 주문 상태 Enum
 */
@Getter
public enum OrderStatus {

    CANCEL("주문 취소"), // 구매자가 주문 취소
    ORDER("주문 수락 대기"), // 주문 수락 대기
    REJECT("거절"), // 판매자 거절
    REQUEST("준비"), // 상품 준비
    ASSIGN("배송 수락"), // 라이더가 상품을 수락
    PICKUP("픽업"), // 라이더가 해당 상품을 수령 -> 배송까지
    COMPLETE("배송완료"); // 배송 완료

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }
}