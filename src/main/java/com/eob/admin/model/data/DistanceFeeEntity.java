package com.eob.admin.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "DISTANCE_FEE")
public class DistanceFeeEntity {

    /**
     * 배송료 고유번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "distance_fee_seq")
    @SequenceGenerator(name = "distance_fee_seq", sequenceName = "distance_fee_seq", allocationSize = 1)
    private long distanceFeeNo;

    /**
     * 거리 (~km까지)
     */
    @Column(nullable = false)
    private int distance;

    /**
     * 배송료
     */
    @Column(nullable = false)
    private int deliveryFee;

    /**
     * 갱신 일시 관리자 update&insert 일시(LocalDateTime.now())
     */
    private LocalDateTime updatedAt;
}
