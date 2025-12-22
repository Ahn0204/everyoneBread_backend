package com.eob.shop.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 전체 입점 신청내역 조회 - 페이징 객체 리턴
     * (예솔 추가)
     */
    Page<ShopEntity> findAll(Pageable pageable);

    /**
     * 폐점 신청내역 조회 - 페이징 객체 리턴
     * (예솔 추가)
     */
    @Query("select s from ShopEntity s where s.status='CLOSE_REVIEW'")
    Page<ShopEntity> findByStatusOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 상품 카테고리&반경 내 해당하는 상점 조회 - 페이징 객체 리턴
     * 
     * (예솔 추가)
     */
    @Query("""
            select distinct s
            from ShopEntity s
            join ProductEntity p on p.shop = s
            where s.status = 'APPLY_APPROVED'
            and p.catName = :category
            and s.latitude between :minLat and :maxLat
            and s.longitude between :minLng and :maxLng
            """)
    Page<ShopEntity> findByProductCatNameAndLocation(
            @Param("category") String category, @Param("minLat") double minLat, @Param("maxLat") double maxLat,
            @Param("minLng") double minLng, @Param("maxLng") double maxLng, Pageable pageable);

    @Query("""
                select distinct s from ShopEntity s
                join ProductEntity p on p.shop = s
                where s.status = 'APPLY_APPROVED'
                and p.catName = :category
            """)
    Page<ShopEntity> findByProductCatName(@Param("category") String category, Pageable pageable);

    /**
     * 상점조회
     * 
     * @param shopNo
     *               return ShopEntity
     */
    ShopEntity findByShopNo(long shopNo);
}
