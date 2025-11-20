package com.eob.member.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.eob.member.model.data.MemberEntity;
import com.eob.shop.model.data.ShopEntity;

@Entity
@Table(name = "WISHLIST")
@Getter
@Setter
public class WishlistEntity {

    /**
     * 찜 고유 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wish_seq")
    @SequenceGenerator(name = "wish_seq", sequenceName = "WISH_SEQ", allocationSize = 1)
    @Column(name = "WISH_NO")
    private Long wishNo;

    /**
     * 회원 고유 번호 FK
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_NO")
    private MemberEntity member;

    /**
     * 상점 고유 번호 FK
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_NO")
    private ShopEntity shop;

    /**
     * 찜 등록 시각
     */
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    /**
     * 찜 상태 (ACTIVE / DELETED)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private WishlistStatus status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = WishlistStatus.ACTIVE;
        }
    }
}
