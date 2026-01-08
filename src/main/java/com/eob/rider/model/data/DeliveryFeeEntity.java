package com.eob.rider.model.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.eob.member.model.data.MemberEntity;
import com.eob.order.model.data.OrderHistoryEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DELIVERY_FEE")
public class DeliveryFeeEntity { // 예솔 작업

    /**
     * 배달비 고유 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_fee_seq")
    @SequenceGenerator(name = "delivery_fee_seq", sequenceName = "delivery_fee_seq", allocationSize = 1)
    private Long feeNo;

    /**
     * 회원 참조번호(라이더)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity riderNo;

    /**
     * 배달 참조 번호
     */
    @ManyToOne
    @JoinColumn(name = "ORDER_NO")
    private OrderHistoryEntity orderNo;

    /**
     * 유형
     */
    @Column(nullable = false)
    private int feeType;

    /**
     * 금액
     */
    private int feeAmount;

    /**
     * 잔액
     */
    private int feeBalance;

    /**
     * 등록 일시
     */
    private LocalDateTime createdAt;

}
