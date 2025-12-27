package com.eob.admin.model.data;

import java.time.LocalDateTime;

import com.eob.member.model.data.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "DISTANCE_FEE_HISTORY")
public class DistanceFeeHistoryEntity {

    /**
     * 변경이력 고유번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "distance_fee_history_seq")
    @SequenceGenerator(name = "distance_fee_history_seq", sequenceName = "distance_fee_history_seq", allocationSize = 1)
    private long distanceFeeHistoryNo;

    /**
     * 배송료 고유번호
     */
    @ManyToOne
    private DistanceFeeEntity distanceFee;
    /**
     * 기존 배송료
     */
    @Column(nullable = false)
    private int deliveryFeeBefore;

    /**
     * 실행한 작업 - C / U / D
     */
    @Column(nullable = false)
    private String operation;

    /**
     * 변경된 배송료
     */
    @Column(nullable = false)
    private int deliveryFeeAfter;
    /**
     * 작업자 멤버 번호
     */
    @ManyToOne
    private MemberEntity member;

    /**
     * 변경 이력이 기록된 일시(LocalDateTime.now())
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
