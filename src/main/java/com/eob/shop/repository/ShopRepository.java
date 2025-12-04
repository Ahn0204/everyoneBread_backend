package com.eob.shop.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eob.member.model.data.MemberEntity;
import com.eob.shop.model.data.ShopApprovalStatus;
import com.eob.shop.model.data.ShopEntity;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {

    // 로그인한 MemberEntity 로 상점 조회
    @Query("select s from ShopEntity s where s.member = :member")
    Optional<ShopEntity> loginShop(@Param("member") MemberEntity member);

    /**
     * 상점명 중복 확인
     */
    boolean existsByShopName(String shopName);

    /**
     * 회원 번호로 상점 조회
     * (s.member.memberNo)
     */
    Optional<ShopEntity> findByMember_MemberNo(Long memberNo);

    /**
     * 상점 status별 조회 - 등록일Desc순
     * (예솔 추가)
     */
    ArrayList<ShopEntity> findByStatusOrderByCreatedAtDesc(ShopApprovalStatus status);
}
