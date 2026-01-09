package com.eob.common.websocket.shop.sender;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.eob.common.websocket.shop.dto.ShopOrderSocketMessage;

import lombok.RequiredArgsConstructor;
/**
 * ShopSocketSender
 *
 * 📌 판매자(Shop)에게 WebSocket(STOMP) 메시지를 "보내는 역할"만 담당하는 클래스
 *
 * ✔ 서버 → 클라이언트(판매자 브라우저) 방향 통신 전용
 * ✔ 비즈니스 로직(Order 처리 등)은 절대 들어가지 않음
 * ✔ Service에서 호출되어 실시간 알림을 전송하는 역할
 *
 * 👉 쉽게 말해
 *    "판매자에게 실시간 알림을 뿌려주는 전담 직원"
 */
@Component
@RequiredArgsConstructor
public class ShopSocketSender {

    /**
     * Spring이 제공하는 STOMP 메시지 전송 도구
     * - WebSocket으로 연결된 클라이언트에게 메시지를 보내기 위해 반드시 필요
     * - 우리가 직접 소켓을 열고 닫지 않아도 이 객체가 모든 처리를 대신해줌
     * - WebSocket의 핵심 객체
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 판매자(상점)에게 "새 주문 알림"을 전송하는 메서드
     * @param shopNo    알림을 받을 가게 번호
     * @param message   실제로 전송할 메시지 데이터 (DTO)
     * 
     * 호출 시점
     * - 소비자가 주문을 생성했을 때 OrderService에서 호출됨
     */
    public void sendNewOrder(Long shopNo, ShopOrderSocketMessage message){
        /**
         * convertAndSend()
         * 특정 STOMP 주소로 메시지를 전송하는 메서드
         * 
         * 형식 : convertAndSend(주소, 메시지)
         * - 이 주소를 "구독"하고 있는 모든 클라이언트가 동시에 메시지를 수신하게 됨
         */
        // "/toAll/shop/{shopNo}"경로로 메시지 전송
        messagingTemplate.convertAndSend("/toAll/shop/" + shopNo, message);
    }
}
