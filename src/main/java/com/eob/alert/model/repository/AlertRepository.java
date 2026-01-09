package com.eob.alert.model.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eob.alert.model.data.AlertEntity;
import com.eob.member.model.data.MemberEntity;

public interface AlertRepository extends JpaRepository<AlertEntity, Long> {

    ArrayList<AlertEntity> findTop10ByToMemberOrderByAlertNoDesc(MemberEntity member);

    List<AlertEntity> findTop10ByToMember_MemberNoAndAlertNoLessThanOrderByAlertNoDesc(Long memberNo, Long lastAlertNo);

    List<AlertEntity> findTop10ByToMemberAndReadYnOrderByAlertNoDesc(MemberEntity member, String string);

    List<AlertEntity> findTop10ByToMember_MemberNoAndAlertNoLessThanAndReadYnOrderByAlertNoDesc(Long memberNo,
            Long lastAlertNo, String string);

    // 읽지 않은 알림 카운트
    int countByToMemberAndReadYn(MemberEntity toMember, String readYn);

}
