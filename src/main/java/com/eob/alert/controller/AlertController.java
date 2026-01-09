package com.eob.alert.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eob.alert.model.data.AlertDTO;
import com.eob.alert.model.data.AlertEntity;
import com.eob.alert.model.service.AlertService;
import com.eob.common.security.CustomSecurityDetail;
import com.eob.member.model.data.MemberEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/alert")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    // 가장 최근 알림 10개 가져오기
    @GetMapping("/recent")
    @ResponseBody
    public List<AlertDTO> recentAlerts(@AuthenticationPrincipal CustomSecurityDetail principal,
            @RequestParam(name = "tab", defaultValue = "all") String tab) {
        MemberEntity member = principal.getMember();
        System.out.println("member : " + member);
        List<AlertDTO> list = this.alertService.recentAlerts(member, tab);
        return list;
    }

    // 다음 알림 10개 가져오기
    @PostMapping("/recent/scroll")
    @ResponseBody
    public List<AlertDTO> ajaxRecentAlerts(@AuthenticationPrincipal CustomSecurityDetail principal,
            @RequestParam(name = "tab", defaultValue = "all") String tab,
            @RequestParam("lastAlertNo") Long lastAlertNo) {
        MemberEntity member = principal.getMember();
        List<AlertDTO> list = this.alertService.ajaxRecentAlerts(member, tab, lastAlertNo);
        return list;
    }

    // 알림 읽음 처리
    @PostMapping("/readAlert")
    @ResponseBody
    public boolean readAlert(@AuthenticationPrincipal CustomSecurityDetail principal,
            @RequestParam("alertNo") Long alertNo) {
        boolean result = this.alertService.readAlert(principal, alertNo);
        return result;
    }

    // 알림 삭제 처리
    @PostMapping("/deleteAlert")
    @ResponseBody
    public boolean deleteAlert(@AuthenticationPrincipal CustomSecurityDetail principal,
            @RequestParam("alertNo") Long alertNo) {
        boolean result = this.alertService.deleteAlert(principal, alertNo);
        return result;
    }

    // 알림 쓰기
    @PostMapping("/sendAlert")
    @ResponseBody
    public String sendAlert(@AuthenticationPrincipal CustomSecurityDetail principal,
            @RequestParam("toMemberNo") Long toMemberNo, @RequestParam("type") String type,
            @RequestParam("typeCode") String typeCode) {
        this.alertService.sendAlert(principal.getMember(), toMemberNo, type, typeCode);

        return "";
    }

    // 알림 갯수 카운트
    @PostMapping("/ajaxCount")
    @ResponseBody
    public int ajaxAlertCount(@AuthenticationPrincipal CustomSecurityDetail principal) {
        int result = 0;
        result = this.alertService.ajaxAlertCount(principal.getMember());
        return result;
    }

}
