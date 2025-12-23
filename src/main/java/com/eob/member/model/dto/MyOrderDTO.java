package com.eob.member.model.dto;

import java.time.LocalDateTime;

import com.eob.order.model.data.OrderStatus;

import lombok.Data;

/**
 * 회원 마이페이지 - 주문내역 목록용 DTO
 */
@Data
public class MyOrderDTO {

    /** 주문 번호 */
    private Long orderNo;

    /** 상점명 */
    private String shopName;

    /** 대표 상품명 */
    private String mainProductName;

    /** 상품 개수 (ex. 3 -> "외 2개") */
    private int productCount;

    /** 썸네일 이미지 */
    private String thumbnail;

    /** 총 결제 금액 */
    private int orderPrice;

    /** 주문 상태 */
    private OrderStatus status;

    /** 주문 시간 */
    private LocalDateTime orderTime;

    /**
     * 주문 상태 한글 라벨 (화면 출력용)
     * ex) COMPLETE → 배송 완료
     */
    public String getStatusLabel() {
        return status != null ? status.getLabel() : "";
    }

    /**
     *  상품 요약 텍스트
     */
    public String getProductSummary(){
        if(productCount <= 1){
            return mainProductName;
        }
        return mainProductName + " 외 " + (productCount -1) + "개";
    }

    /**
     * 리뷰 작성 가능 여부
     */
    public boolean isCanWriteReview(){
        return status == OrderStatus.COMPLETE;
    }

    /**
     * 재구매 가능 여부
     */
    public boolean isCanReorder(){
        return status == OrderStatus.COMPLETE;
    }
}
