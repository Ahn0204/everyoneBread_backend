package com.eob.rider.model.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.eob.member.model.data.MemberEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rider")
public class RiderEntity {

    /**
     * 라이더 고유 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rider_seq")
    @SequenceGenerator(name = "rider_seq", sequenceName = "rider_seq", allocationSize = 1)
    private Long riderNo;

    /**
     * 회원 참조번호
     */
    @OneToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    /**
     * 운전면허 등록번호
     */
    private String riderLicense;

    /**
     * 운전면허 발급일
     */
    private LocalDate licenseCreatedAt;

    /**
     * 라이더 회원의 등록일
     */
    private LocalDateTime createdAt;

    /**
     * 라이더 회원의 가입 승인 일시
     */
    private LocalDateTime approvedAt;

    /**
     * 라이더 회원의 가입 승인 상태
     */
    @Enumerated(EnumType.STRING)
    private ApprovalStatus aStatus = ApprovalStatus.PENDING;

    /**
     * 라이더 주소 기반 좌표 (경도 X)
     */
    private double longitude;

    /**
     * 라이더 주소 기반 좌표 (위도 Y)
     */
    private double latitude;

    @OneToOne(cascade = CascadeType.ALL, // 부모 삭제시 자식도 삭제
            orphanRemoval = true, // 부모와의 연결이 끊기면 자식 삭제
            fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_file_no")
    private RiderLicenseFileEntity riderLicenseFile;
}
