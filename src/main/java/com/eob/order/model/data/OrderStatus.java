package com.eob.order.model.data;

import lombok.Getter;

/**
 * 주문 상태 Enum
 */
@Getter
public enum OrderStatus {

    WAIT("대기"),               // 주문 접수 대기 (수락/거절)
    PREPARE("준비"),            // 상품 준비
    DELIVERING("배송중"),       // 배송 중
    COMPLETE("완료"),           // 배송 완료
    REJECT("거절");             // 판매자 거절

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }
}