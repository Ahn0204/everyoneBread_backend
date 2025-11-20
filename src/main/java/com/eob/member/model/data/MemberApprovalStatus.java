package com.eob.member.model.data;

import lombok.Getter;

/**
 * 회원 상태 (Enum)
 * 
 * 회원 상태 흐름 : 
 * 1) PENDING : 가입 대기 상태
 * 2) ACTIVE : 정상적으로 서비스 이용 가능한 상태
 * 3) SUSPENDED : 관리자에 의해 제한된 계정 (로그인 불가)
 * 4) WITHDRAW : 회원이 탈퇴한 상태 (비활성 처리)
 * 5) INACTIVE : 폐점/휴면 전환 등으로 계정은 존재하지만 활동 불가한 상태
 * 
 * 서비스 로직에는 Enum을 사용
 * DB에는 Enum 이름 그대로 저장하는 방식이 가장 안전함.
 */
@Getter
public enum MemberApprovalStatus {

    PENDING("가입 대기"),   // 가입 절차가 완료되지 않은 상태
    ACTIVE("정상"),         // 정상 회원
    SUSPENDED("정지"),      // 관리자에 의해 정지된 상태
    WITHDRAW("탈퇴"),       // 회원 탈퇴 상태
    INACTIVE("비활성");     // 휴면/폐점 등으로 계정은 존재하나 활동 불가한 상태

    // 사용자에게 보여줄 설명
    private final String description;

    MemberApprovalStatus(String description){
        this.description = description;
    }
}
