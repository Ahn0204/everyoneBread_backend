package com.eob.admin.model.data;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DistanceFeeForm { // 배송비 변경 폼

    /**
     * 거리 (~km까지)
     */
    @NotEmpty(message = "거리를 입력해주세요")
    private int distance;

    /**
     * 배송료
     */
    @NotEmpty(message = "배송비를 입력해주세요")
    private int deliveryFee;

    /**
     * crud 유형 (어떤 작업인지)
     */
    private String operation;
}
