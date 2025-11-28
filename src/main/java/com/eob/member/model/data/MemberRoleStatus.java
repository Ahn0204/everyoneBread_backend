package com.eob.member.model.data;

import lombok.Getter;

/**
 * 로그인 후 어디로 보낼지 / 어떤 권한을 줄지
 * 권한과 이동 경로를 지정하는데 사용한다.
 * 
 * 회원 권한 (Enum)
 * 
 * 권한 설명 :
 * - ROLE_USER : 일반 소비자 (회원 기능)
 * - ROLE_SHOP : 판매자(상점) - 상품 등록/관리, 주문 처리 가능
 * - ROLE_RIDER : 배달 기사 - 배달/수령 상태 처리
 * - ROLE_ADMIN : 관리자 - 전체 회원/상점/상품 관리, 승인/반려 처리
 * 
 * 서비스 로직과 Security에서 모두 Enum 사용
 * DB에는 Enum의 name() 값 그대로 STRING 형태로 저장하는 것이 가장 안전함.
 */
@Getter
public enum MemberRoleStatus {

    USER("일반 회원"), // 일반 소비자 권한
    SHOP("상점"),      // 판매자 (상점) 권한
    RIDER("라이더"),   // 배달 기사 (라이더) 권한
    ADMIN("관리자");   // 관리자 권한

    // 화면/로그에서 사용할 설명
    private final String description;

    MemberRoleStatus(String description){
        this.description = description;
    }
}
