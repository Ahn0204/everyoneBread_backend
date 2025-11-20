package com.eob.member.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "WITHDRAW_HISTORY")
@Getter
@Setter
public class WithdrawHistoryEntity {

    /**
     * 탈퇴 이력 고유 번호 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "withdraw_seq")
    @SequenceGenerator(name = "withdraw_seq", sequenceName = "WITHDRAW_SEQ", allocationSize = 1)
    @Column(name = "WITHDRAW_NO")
    private Long withdrawNo;

    /**
     * 회원 고유 번호 FK
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_NO")
    private MemberEntity member;

    /**
     * 탈퇴 사유
     */
    @Column(name = "REASON", length = 500)
    private String reason;

    /**
     * 회원 권한
     * MEMBER / SHOP
     */
    @Column(name = "ROLE")
    private String role;

    /** 
     * 탈퇴 완료 시각
     */
    @Column(name = "WITHDRAWN_AT")
    private LocalDateTime withdrawnAt;

    @PrePersist
    protected void onCreate() {
        this.withdrawnAt = LocalDateTime.now();
    }
}
