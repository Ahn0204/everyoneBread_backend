package com.eob.common.webSocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//STOMP기능 활성화
@EnableWebSocketMessageBroker
// 설정 클래스임을 알림
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // 인터페이스WebSocketConfigurer를 구현하여 웹소켓 통로가 될 url을 지정한다.
    // 웹소켓 연결은 http가 아닌 웹소켓 프로토콜로, ws://로 한다.

    // 메소드 오버라이드하여 웹소켓의 엔드포인트(url) 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // 클라이언트가 접속할 웹소켓 URL 설정 (ws://localhost:8080/ws/alert)
        // .withSockJS(); 웹소켓을 지원하지 않는 브라우저를 위한 대체 통신을 허용함
        registry.addEndpoint("ws/alert").withSockJS();
    }

    // STOMP 규칙 설정(메세지를 보내는 경로)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 규칙.서버에서 클라이언트로 보낼 때("1:1 메세지 경로", "1:N 메세지 경로")
        registry.enableSimpleBroker("/to", "/toAll");
        // 규칙.특정 사용자에게 보낼 때("")
        registry.setUserDestinationPrefix("/member");
        // 규칙.클라이언트에서 서버로 보낼 때("컨트롤러의 @MassageMapping에서 받을 경로")
        registry.setApplicationDestinationPrefixes("/app");
    }
}
