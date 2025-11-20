package com.eob.member.model.data;

import lombok.Getter;

/**
 * 리뷰 상태 (Enum)
 * 
 * 상태 설명 : 
 * - POSTED : 게시된 정상 후기
 * - DELETED : 사용자가 삭제한 후기
 * - PRIVATE : 작성자가 비공개한 후기
 * 
 * DB에는 Enum의 name() 그대로 저장하는 것이 안전함.
 */
@Getter
public enum ReviewStatus {

    POSTED("게시"),
    DELETED("삭제"),
    PRIVATE("비공개");

    private final String description;

    ReviewStatus(String description){
        this.description = description;
    }
}
