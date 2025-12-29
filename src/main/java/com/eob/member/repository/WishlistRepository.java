package com.eob.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eob.member.model.data.WishlistEntity;
import com.eob.member.model.data.WishlistStatus;

public interface WishlistRepository extends JpaRepository<WishlistEntity, Long> {

    /**
     * [찜 토글 공통]
     * - 회원이 특정 상점을 이미 찜했는지 조회
     * - ACTIVE / DELETED 상관없이 row 존재 여부 확인
     *
     * 사용 페이지:
     * - 상점 상세 페이지 (하트 클릭)
     * - 마이페이지 즐겨찾기 해제
     */
    @Query("select w from WishlistEntity w where w.member.memberNo = :memberNo and w.shop.shopNo = :shopNo")
    Optional<WishlistEntity> findMyWish( @Param("memberNo") Long memberNo, @Param("shopNo") Long shopNo );

    /**
     * [마이페이지 - 즐겨찾기 목록]
     * - 로그인 회원의 ACTIVE 상태 찜 목록 조회
     * - 최신 찜 순 정렬
     *
     * 사용 페이지:
     * - 마이페이지 > 즐겨찾기
     */
    @Query("select w from WishlistEntity w join fetch w.shop where w.member.memberNo = :memberNo and w.status = :status order by w.createdAt desc")
    List<WishlistEntity> findMyActiveWishes( @Param("memberNo") Long memberNo, @Param("status") WishlistStatus status );

    /**
     * [상점 찜 수 집계]
     * - 특정 상점의 ACTIVE 찜 개수
     *
     * 사용 페이지:
     * - 상점 상세 페이지
     * - 관리자 통계 / 인기 상점 정렬
     */
    @Query("select count(w) from WishlistEntity w where w.shop.shopNo = :shopNo and w.status = :status")
    long countActiveByShop(  @Param("shopNo") Long shopNo, @Param("status") WishlistStatus status );
}