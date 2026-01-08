package com.eob.admin.model.data;

import java.time.LocalDateTime;

import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "INQUIRY")
@Data
public class InquiryEntity {
    /**
     * 일반 문의 고유번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inquiry_seq")
    @SequenceGenerator(name = "inquiry_seq", sequenceName = "inquiry_seq", allocationSize = 1)
    private long inquiryNo;

    @ManyToOne
    @JoinColumn(name = "MEMBER_NO", nullable = false)
    private MemberEntity member; // 작성자 멤버 엔티티

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRoleStatus role; // 작성자 role

    @Column(nullable = false)
    private String question; // 문의 내용

    @Column(nullable = false)
    private LocalDateTime createdAt; // 작성 일시

    private String answer; // 답변 내용

    private LocalDateTime answeredAt; // 답변 일시

    @Column(nullable = false)
    private String status = "n"; // 답변 상태 n - 미답변or y - 답변

}
