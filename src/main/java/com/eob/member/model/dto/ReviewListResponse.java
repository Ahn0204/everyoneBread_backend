package com.eob.member.model.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * - 마이페이지 리뷰 리스트 화면 전용 DTO
 * - ReviewEntity를 그대로 노출하지 않기 위해 사용
 */
@Getter
@Setter
public class ReviewListResponse {

    private Long reviewNo;           // 리뷰 번호
    private Long orderNo;            // 주문 번호
    private String productName;      // 상품명
    private String productImg;       // 상품 이미지
    private Double rating;          // 별점
    private String content;          // 리뷰 내용
    private LocalDateTime createdAt; // 작성일
}