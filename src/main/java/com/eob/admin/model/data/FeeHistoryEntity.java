package com.eob.admin.model.data;

import java.time.LocalDateTime;

import com.eob.member.model.data.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "FEE_HISTORY")
@Entity
public class FeeHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fee_history_seq")
    @SequenceGenerator(name = "fee_history_seq", sequenceName = "fee_history_seq", allocationSize = 1)
    private long feeNo;

    @Column(nullable = false)
    private double shopFeeRatio;

    @Column(nullable = false)
    private double riderFeeRatio;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "member_no")
    private MemberEntity memberNo;
}
