package com.eob.shop.model.data;

import lombok.Getter;

/**
 * 상점 심사 상태 (입점/폐점) Enum
 * 
 * 상점의 상태 흐름 :
 * 1) 입점 검토
 * 2) 입점 반려 or 입점 승인
 * 3) 운영 중에는 "폐점 신청" 발생 -> 폐점 검토 상태가 됨
 * 4) 이후 폐점 반려 or 폐점 승인
 * 
 * 화면/로직에서는 Enum을 사용하는 방식
 */
@Getter
public enum ShopApprovalStatus {

    APPLY_REVIEW("입점 검토"), // 입점 검토 : 관리자가 심사하는 상태
    APPLY_REJECT("입점 반려"), // 입점 반려 : 보완 필요 또는 불가 판정
    CLOSE_REVIEW("폐점 검토"), // 폐점 검토 : 폐점 요청 시 상태 변경
    CLOSE_REJECT("폐점 반려"), // 폐점 반려 : 폐점 승인 거부
    APPLY_APPROVED("입점 승인"), // 입점 승인 : 정상 영업 가능 상태
    CLOSE_APPROVED("폐점 승인"); // 폐점 승인 : 상점 운영 종료

    // 사용자에게 보여줄 상태 설명
    private final String description;

    ShopApprovalStatus(String description) {
        this.description = description;
    }
}
