package com.eob.order.model.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 상세 - 상품 단위 DTO
 * (모달에서 상품 목록 표시용)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    
    /** 상품명 */
    private String productName;

    /** 수량 */
    private int quantity;

    /** 상품 단가 (또는 소계 금액) */
    private int price;
}