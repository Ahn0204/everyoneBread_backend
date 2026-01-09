package com.eob.alert.model.data;

import java.time.LocalDateTime;

import com.eob.member.model.data.MemberEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "alert")
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alert_seq")
    @SequenceGenerator(name = "alert_seq", sequenceName = "alert_seq", allocationSize = 1)
    // 알림 고유 번호
    private Long alertNo;

    /** 수신자(받는사람) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TO_MEMBER_NO", nullable = false)
    private MemberEntity toMember;

    /** 발신자(보내는사람) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FROM_MEMBER_NO", nullable = false)
    private MemberEntity fromMember;

    /** 제목 */
    private String title;
    /** 내용 */
    private String content;

    /** 대분류 */
    private String type;
    /** 소분류 */
    private String typeCode;

    /** 이동 URL */
    private String linkUrl;
    /** 읽음 여부 (Y/N) */
    private String readYn;

    /** 읽은 시간 */
    private LocalDateTime readAt;

    /** 생성 시간 */
    private LocalDateTime createdAt;

    /** 생성시간 자동 세팅 */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
