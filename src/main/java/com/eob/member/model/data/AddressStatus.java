package com.eob.member.model.data;

import lombok.Getter;

/**
 * 배송지 상태 (Enum)
 * 
 * 상태 설명 : 
 * - ACTIVE : 활성 배송지
 * - DELETED : 사용자가 삭제한 배송지
 * 
 * DB에는 Enum의 name() 그대로 저장하는 것이 가장 안전함.
 */
@Getter
public enum AddressStatus {

    ACTIVE("활성"),
    DELETED("삭제");

    private final String description;

    AddressStatus(String description){
        this.description = description;
    }
}
