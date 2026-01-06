package com.eob.admin.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eob.admin.model.service.AdminService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SettleScheduler {

    private final AdminService adminService;

    // 매주 월요일 오전 10시 상점 정산
    @Scheduled(cron = "0 0 10 * * WED")
    public void shopSettlement() {

        adminService.shopSettlement();
        // 실행 잘 됐으면 관리자에게 웹소켓 알림 보내기

    }

    // 매주 목요일 오전 10시 라이더 정산
    // => 라이더엔티티에 정산 계좌 추가되면 스케줄러에서 사용
    // @Scheduled(cron = "0 0 10 * * THU")
    // public void riderSettlement() {
    // adminService.riderSettlement();
    // // 실행 잘 됐으면 관리자에게 웹소켓 알림 보내기
    // }
}
