package com.eob.admin.model.data;

import java.time.LocalDateTime;

import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.MemberRoleStatus;
import com.eob.member.model.data.ReviewEntity;
import com.eob.order.model.data.OrderHistoryEntity;

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
@Table(name = "BAN_INQUIRY")
@Data
public class BanInquiryEntity {
    /**
     * 일반 문의 고유번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ban_inquiry_seq")
    @SequenceGenerator(name = "ban_inquiry_seq", sequenceName = "ban_inquiry_seq", allocationSize = 1)
    private long banInquiryNo;

    @ManyToOne
    @JoinColumn(name = "MEMBER_NO", nullable = false)
    private MemberEntity member; // 작성자 멤버 엔티티(소비자 -> 소비자 본인, 상점 -> 상점 본인)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRoleStatus role; // 작성자 role

    @Column(nullable = false)
    private String question; // 문의 내용

    @Column(nullable = true)
    private String file; // 파일명

    @ManyToOne
    @JoinColumn(name = "BAN_MEMBER_NO", nullable = true)
    private MemberEntity banMember; // 제재 대상 (소비자 -> 제재될 상점, 상점 -> 제재될 소비자)

    @ManyToOne
    @JoinColumn(name = "ORDER_NO", nullable = false)
    private OrderHistoryEntity order; // 제재 주문

    @ManyToOne
    @JoinColumn(name = "REVIEW_NO", nullable = true)
    private ReviewEntity review; // 리뷰

    @Column(nullable = false)
    private LocalDateTime createdAt; // 작성 일시

    private String answer; // 답변 내용

    private LocalDateTime answeredAt; // 답변 일시

    @Column(nullable = false)
    private String status = "n"; // 답변 상태 n - 미답변or y - 답변

}
