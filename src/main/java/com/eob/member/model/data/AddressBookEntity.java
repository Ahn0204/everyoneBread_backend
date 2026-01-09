package com.eob.member.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ADDRESS_BOOK")
@Getter
@Setter
public class AddressBookEntity {

    /**
     * 배송지 고유 번호 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "ADDRESS_SEQ", allocationSize = 1)
    @Column(name = "ADDRESS_NO")
    private Long addressNo;

    /**
     * 회원 번호 FK
     */
    // 추가 2026.01.09
    @Column(name = "MEMBER_NO", nullable = false)
    private Long memberNo;

     /**
     * 별칭 (집/회사 등)
     */
    @Column(name = "ALIAS", length = 50)
    private String alias;

    /**
     * 주소
     */
    @Column(name = "ADDRESS", nullable = false, length = 255)
    private String address;

    /**
     * 기본 배송지 여부 (Y/N 대신 boolean 사용)
     */
    @Column(name = "IS_DEFAULT")
    private Boolean isDefault;

    /**
     * 배송지 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private AddressStatus status;

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

    /**
     * 등록일
     */
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = AddressStatus.ACTIVE;
        }

        if (this.isDefault == null) {
            this.isDefault = false;
        }
    }   
}
