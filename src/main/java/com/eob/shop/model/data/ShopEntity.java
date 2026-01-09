package com.eob.shop.model.data;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.eob.member.model.data.MemberEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SHOP")
@Getter
@Setter
public class ShopEntity {

    /**
     * 상점 고유 번호 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_seq")
    @SequenceGenerator(name = "shop_seq", sequenceName = "SHOP_SEQ", allocationSize = 1)
    @Column(name = "SHOP_NO")
    private Long shopNo;

    /**
     * 회원 고유 번호 FK
     */
    @OneToOne
    @JoinColumn(name = "MEMBER_NO", nullable = false)
    @JsonIgnore
    private MemberEntity member;

    /**
     * 상점 이름
     */
    @Column(name = "SHOP_NAME", nullable = false, unique = true)
    private String shopName;

    /**
     * 판매자 이름
     */
    @Column(name = "SELLER_NAME", nullable = false)
    private String sellerName;

    /**
     * 사업자 등록 번호
     */
    @Column(name = "BIZ_NO", nullable = false)
    private String bizNo;

    /**
     * 상점 주소
     */
    @Column(name = "SHOP_ADDRESS", nullable = false)
    private String shopAddress;

    /**
     * 상점 대표 이미지
     */
    private String shopImg;

    /**
     * 상점 한줄 소개
     */
    private String shopIntro;

    /**
     * 영업 시작 시간
     */
    private String openTime;

    /**
     * 영업 종료 시간
     */
    private String closeTime;

    /**
     * 휴무일
     */
    private String holiday;

    /**
     * 평균 별점
     */
    private Double avgRating;

    /**
     * 심사 상태
     */
    @Enumerated(EnumType.STRING)
    private ShopApprovalStatus status;

    /**
     * 은행명
     */
    private String bankName;

    /**
     * 예금주명
     */
    private String accountName;

    /**
     * 계좌번호
     */
    private String accountNo;

    /**
     * 등록일
     */
    private LocalDateTime createdAt;

    /**
     * 입점 승인 일시
     */
    private LocalDateTime approvedDate;

    /**
     * 폐점 사유
     */
    private String closedReason;

    /**
     * 폐점 일시
     */
    private LocalDateTime closedAt;

    /**
     * 폐점 신청 일시
     */
    private LocalDateTime closedRequestAt;

    /**
     * 보완 사유
     */
    private String rejectReason;

    /**
     * 파일 경로
     */
    private String bizImg;

    @PrePersist
    public void PrePersist() {

        // 생성일 자동 저장
        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * 상점 위치 (좌표형식으로 저장됨)
     */
    // @JdbcTypeCode(SqlTypes.STRUCT) // DDL생성(ALTER문) 차단 + 타입 지정
    // @Column(name = "LOCATION", nullable = false, columnDefinition =
    // "MDSYS.SDO_GEOMETRY") // hibernate가 이 컬럼을 수정하지 않도록
    // // 컬럼 명시
    // private Point location;

    /**
     * 상점 위치 - 경도
     */
    private Double longitude;

    /**
     * 상점 위치 - 위도
     */
    private Double latitude;

    /**
     * 상점과 사용자 위치간의 직선 거리 출력용(DB에 저장되지않음)
     */
    @jakarta.persistence.Transient
    private String distance;
}
