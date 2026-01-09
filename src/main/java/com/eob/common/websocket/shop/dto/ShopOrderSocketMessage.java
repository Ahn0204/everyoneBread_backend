package com.eob.common.websocket.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ShopOrderSocketMessage
 *
 * 📌 이 클래스는 "판매자(Shop)에게 전달되는
 *     주문 관련 WebSocket 메시지"를 담는 DTO(Data Transfer Object)이다.
 *
 * ✔ HTTP Controller에서 사용하는 요청/응답 DTO와 완전히 분리된 객체
 * ✔ STOMP(WebSocket) 통신에서만 사용
 * ✔ 서버 → 판매자 브라우저 방향으로 메시지를 보낼 때 사용
 *
 * 👉 즉,
 *    "주문이 발생했을 때 판매자 화면에 실시간으로 알려주기 위한 데이터 묶음"
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderSocketMessage {

    /**
     * 주문 번호
     * - 어떤 주문에 다한 알림인지 구분하기 위한 값
     */
    private Long orderNo;

    /**
     * 가게 번호
     * - 어떤 가게(판매자)에게 보낼 알림인지 구분하기 위한 값
     */
    private Long shopNo;

    /**
     * 알림 메시지 내용
     * - 판매자에게 실제로 보여줄 문구
     * - 예: "새로운 주문이 들어왔습니다!"
     */
    private String message;

    /**
     * 주문 상태
     * - 실시간 주문 상태를 프론트에서 바로 반영하기 위한 값
     */
    private String orderStatus;

}