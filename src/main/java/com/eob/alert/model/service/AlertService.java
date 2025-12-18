package com.eob.alert.model.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.eob.alert.model.data.AlertDTO;
import com.eob.alert.model.data.AlertEntity;
import com.eob.alert.model.repository.AlertRepository;
import com.eob.common.security.CustomSecurityDetail;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice.Return;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final MemberRepository memberRepository;

    /**
     * 회원 기준 최근 알람 10개 가져오기
     * 
     * @param tab
     */
    public List<AlertDTO> recentAlerts(MemberEntity member, String tab) {

        List<AlertDTO> result = new ArrayList<>();
        List<AlertEntity> list;
        if ("unread".equals(tab)) {
            list = this.alertRepository.findTop10ByToMemberAndReadYnOrderByAlertNoDesc(member, "N");
        } else {
            list = this.alertRepository.findTop10ByToMemberOrderByAlertNoDesc(member);
        }

        for (AlertEntity alert : list) {
            result.add(AlertDTO.from(alert));
        }

        return result;
    }

    /**
     * 무한 스크롤로 10개 알림 추가로 가져오기
     * 
     * @param tab
     */
    public List<AlertDTO> ajaxRecentAlerts(MemberEntity member, String tab, Long lastAlertNo) {
        List<AlertEntity> list;
        if ("unread".equals(tab)) {

            list = this.alertRepository
                    .findTop10ByToMember_MemberNoAndAlertNoLessThanAndReadYnOrderByAlertNoDesc(member.getMemberNo(),
                            lastAlertNo, "N");
        } else {

            list = this.alertRepository
                    .findTop10ByToMember_MemberNoAndAlertNoLessThanOrderByAlertNoDesc(member.getMemberNo(),
                            lastAlertNo);
        }

        List<AlertDTO> result = new ArrayList<>();
        for (AlertEntity alert : list) {
            result.add(AlertDTO.from(alert));
        }
        return result;
    }

    /** 알림 읽기 */
    @Transactional
    public boolean readAlert(CustomSecurityDetail principal, Long alertNo) {
        Optional<AlertEntity> _alert = this.alertRepository.findById(alertNo);
        if (!_alert.isPresent()) {
            return false;
        }
        System.out.println(_alert.get().getToMember().getMemberNo());
        System.out.println(principal.getMember().getMemberNo());
        // 알림의 수신자와 로그인한 회원이 같지 않을경우
        if (!_alert.get().getToMember().getMemberNo().equals(principal.getMember().getMemberNo())) {
            return false;
        }

        AlertEntity alert = _alert.get();
        if ("Y".equals(alert.getReadYn())) {
            return true;
        }
        alert.setReadYn("Y");
        alert.setReadAt(LocalDateTime.now());
        return true;
    }

    @Transactional
    public void sendAlert(MemberEntity fromMember, Long toMemberNo, String type, String typeCode) {
        AlertEntity alert = new AlertEntity();
        Optional<MemberEntity> _toMember = this.memberRepository.findById(toMemberNo);
        if (!_toMember.isPresent()) {
            return;
        }
        String url = "";
        String title = "";
        String content = "";
        // 아래 스위치 문에서 해당 type 과 typeCode에 따른 url , title ,content 삽입하기
        switch (type) {
            case "ORDER":
                title = "주문";
                switch (typeCode) {
                    case "ACCEPTED":
                        url = "/order/";
                        content = "주문이 수락되었습니다.";
                        break;

                    default:
                        break;
                }
                break;
            case "DELIVERY":
                title = "배송";
                switch (typeCode) {
                    case "":

                        break;

                    default:
                        break;
                }
                break;
            case "INQUIRY":
                title = "문의";
                switch (typeCode) {
                    case "":

                        break;

                    default:
                        break;
                }
                break;
            case "REVIEW":
                title = "리뷰";
                switch (typeCode) {
                    case "":

                        break;

                    default:
                        break;
                }
                break;
            case "APPROVAL":
                title = "가입관련";
                switch (typeCode) {
                    case "":

                        break;

                    default:
                        break;
                }
                break;
            case "SYSTEM":
                title = "시스템";
                switch (typeCode) {
                    case "":

                        break;

                    default:
                        break;
                }
                break;
            default:
                break;
        }
        alert.setTitle(title);
        alert.setContent(content);
        alert.setLinkUrl(url);
        alert.setToMember(_toMember.get());
        alert.setFromMember(fromMember);
        alert.setReadYn("N");
        alert.setType(type);
        alert.setTypeCode(typeCode);

        this.alertRepository.save(alert);

    }

}
