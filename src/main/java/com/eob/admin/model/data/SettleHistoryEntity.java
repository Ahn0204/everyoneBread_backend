package com.eob.admin.model.data;

import java.time.LocalDateTime;

import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "SETTLE_HISTORY")
@Entity
@Data
public class SettleHistoryEntity { // 정산완료 내역

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "settle_history_seq")
    @SequenceGenerator(name = "settle_history_seq", sequenceName = "settle_history_seq", allocationSize = 1)
    private long settleHistoryNo; // 정산 완료 내역 고유번호

    @ManyToOne
    @JoinColumn(name = "member_no")
    private MemberEntity memberNo;

    @Column(nullable = false)
    private MemberRoleStatus role; // 회원 권한

    @Column(nullable = false)
    private String settleName; // 예금주명

    @Column(nullable = false)
    private String bank; // 은행명

    @Column(nullable = false)
    private String account; // 계좌번호

    @Column(nullable = false)
    private long currentAmount; // 보유 금액(정산 전)

    @Column(nullable = false)
    private int feeAmount; // 수수료

    @Column(nullable = false)
    private long settleAmount; // 정산된 금액

    @Column(nullable = false)
    private LocalDateTime createdAt; // 정산 일시
}
