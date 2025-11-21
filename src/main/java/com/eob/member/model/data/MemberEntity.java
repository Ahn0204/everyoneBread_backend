package com.eob.member.model.data;


import java.time.LocalDateTime;

import com.eob.rider.model.data.RiderEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.FetchType;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MEMBER")
@Getter
@Setter
public class MemberEntity {

    // 회원 고유 번호
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq")
    @SequenceGenerator(name = "member_seq", sequenceName = "MEMBER_SEQ", allocationSize = 1)
    private Long memberNo;

    /**
     * 회원 아이디
     */
    @Column(name = "MEMBER_ID", nullable = false, unique = true)
    private String memberId;

    /**
     * 회원 비밀번호 
     * */
    @Column(name = "MEMBER_PW", nullable = false)
    private String memberPw;

    /**
     * 회원 이름
     */
    @Column(name = "MEMBER_NAME", nullable = false)
    private String memberName;

    /**
     * 회원 주민번호
     * 생년월일과 성별 구분
     */
    @Column(name = "MEMBER_JUMIN", nullable = false)
    private String memberJumin;

    /**
     * 회원 휴대폰 번호
     */
    @Column(name = "MEMBER_PHONE", nullable = false)
    private String memberPhone;

    /**
     * 회원 이메일
     */
    @Column(name = "MEMBER_EMAIL", nullable = false)
    private String memberEmail;

    /**
     * 회원 주소
     */
    @Column(name = "MEMBER_ADDRESS", nullable = false)
    private String memberAddress;

    /**
     * 회원 상태
     * PENDING=가입 대기 | ACTIVE=사용가능 | SUSPENDED=정지 | WITHDRAW=탈퇴 | INACTIVE=폐점 후 계정 존재
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private MemberApprovalStatus status;

    /**
     * 회원가입 등록일
     */
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    /**
     * 회원 권한
     * USER=소비자 | SHOP=상점 | ADMIN=관리자 | RIDER=배달기사
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBER_ROLE")
    private MemberRoleStatus memberRole;

    /**
     * 위도
     */
    @Column(name = "LATITUDE")
    private Double latitude;

    /**
     * 경도
     */
    @Column(name = "LONGITUDE")
    private Double longitude;

    // 회원가입 시 createdAt 자동 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if(this.status == null){
            this.status = MemberApprovalStatus.PENDING;
        }
    }

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private RiderEntity rider;
}
