package com.eob.rider.model.data;

/**
 * 라이더 회원 가입 승인 상태를 나타내는 Enum
 * 
 * <ul>
 *   <li><b>PENDING</b> – 승인 대기</li>
 *   <li><b>UNDER_REVIEW</b> – 검토 중</li>
 *   <li><b>REVISION_REQUIRED</b> – 보완 요청</li>
 *   <li><b>APPROVED</b> – 승인 완료</li>
 * </ul>
 */
public enum ApprovalStatus {

    PENDING("대기"),                // 대기
    UNDER_REVIEW("검토중"),           // 검토중
    REVISION_REQUIRED("보완요청"),         // 보완요청
    APPROVED("승인");                // 승인

    // 한글 라벨 저장 변수
    private final String label;

    // 생성자
    ApprovalStatus(String label){
        this.label = label;
    }

    // 외부에서 한글 이름 가져올 때 
    public String getLabel(){
        return label;
    }

    /*
     * 사용 예시
     * ApprovalStatus aStatus = ApprovalStatus.PENDING;
     * 
     * System.out.println(aStatus);             // PENDING
     * System.out.println(aStatus.getLabel);    // 대기
     */
    
}
