package com.eob.order.model.data;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 마이페이지 - 주문 상세 조회 응답 DTO
 * (주문 상세 모달 전용)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {

    /** 주문 번호 */
    private Long orderNo;

    /** 주문 상태 (WAIT, PREPARE, DELIVERING, COMPLETE 등) */
    private OrderStatus status;

    /** 총 주문 금액 */
    private int orderPrice;

    /** 배송비 */
    private int deliveryFee;

    /** 배송 주소 */
    private String address;

    /** 주문 상품 목록 */
    private List<OrderItemDTO> items;
}