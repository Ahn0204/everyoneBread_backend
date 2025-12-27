package com.eob.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 마이페이지 - 즐겨찾기 응답 DTO
 * (Shop 정보만 담는다)
 */
@Getter
@AllArgsConstructor
public class WishResponseDTO {

    /** 상점 번호 */
    private Long shopNo;

    /** 상점명 */
    private String shopName;

    /** 상점 이미지 */
    private String shopImg;

}