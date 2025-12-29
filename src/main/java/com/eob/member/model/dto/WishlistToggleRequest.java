package com.eob.member.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 즐겨찾기(찜) 토글 요청 DTO
 *
 * 사용 API:
 * - POST /wishlist/toggle
 *
 * 역할:
 * - 클라이언트(JS) -> 서버로 shopNo 전달
 * - memberNo는 SecurityContext에서 추출
 */
@Getter
@NoArgsConstructor
public class WishlistToggleRequest {

    /** 상점 고유 번호 */
    private Long shopNo;
}
