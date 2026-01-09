package com.eob.common.websocket.admin.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

//컨트롤러의 모든 매핑이 @ResponseBody가 됨
@RestController
@RequestMapping("/ws")
@RequiredArgsConstructor
public class WebSocketController {

    // STOMP 메세지 발송 전용 객체 (STOMP의 핵심 추상화 객체)
    // 하는 일: 웹소켓 연결 여부 판단, 연결중인 클라이언트 탐색, 메세지 처리..
    private final SimpMessagingTemplate messagingTemplat;

}
