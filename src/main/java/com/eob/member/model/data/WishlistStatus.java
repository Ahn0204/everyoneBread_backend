package com.eob.member.model.data;

import lombok.Getter;

/**
 * 찜(Wishlist) 상태 Enum
 *
 * 상태 흐름:
 * - ACTIVE  : 사용자가 현재 찜한 상태
 * - DELETED : 사용자가 찜 제거(삭제)한 상태
 *
 * DB에는 Enum의 name() 값 그대로 저장하는 것이 가장 안전하다.
 */
@Getter
public enum WishlistStatus {

    /** 사용자가 해당 상점을 찜한 활성 상태 */
    ACTIVE("활성"),

    /** 사용자가 찜을 해제하여 비활성(삭제)된 상태 */
    DELETED("삭제");

    /** 화면 및 로그용 설명 */
    private final String description;

    WishlistStatus(String description) {
        this.description = description;
    }
}
