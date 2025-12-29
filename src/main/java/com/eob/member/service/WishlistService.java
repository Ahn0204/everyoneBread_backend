package com.eob.member.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eob.member.model.data.MemberEntity;
import com.eob.member.model.data.WishlistEntity;
import com.eob.member.model.data.WishlistStatus;
import com.eob.member.model.dto.WishResponseDTO;
import com.eob.member.repository.MemberRepository;
import com.eob.member.repository.WishlistRepository;
import com.eob.shop.model.data.ShopEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final MemberRepository memberRepository;

    /* 마이페이지 - 즐겨찾기 목록 조회 */
    @Transactional(readOnly = true)
    public List<WishResponseDTO> getMyActiveWishlist(Long memberNo) {

        /* 로그인 회원 기준 + ACTIVE 상태 찜 목록 조회 */
        List<WishlistEntity> wishEntities = wishlistRepository.findMyActiveWishes(memberNo, WishlistStatus.ACTIVE);

        /* Entity -> 마이페이지 전용 DTO 변환 (Shop 정보만 담는다) */
        return wishEntities.stream()
                .map(wish -> new WishResponseDTO(
                        wish.getShop().getShopNo(),     // 상점 번호
                        wish.getShop().getShopName(),   // 상점명
                        wish.getShop().getShopImg()     // 상점 이미지
                ))
                .collect(Collectors.toList());
    }

    /* 즐겨찾기(찜) 토글
     * 규칙:
     * 1) 찜 이력 없음        → INSERT (ACTIVE)
     * 2) STATUS = ACTIVE     → DELETED
     * 3) STATUS = DELETED    → ACTIVE
     */
    @Transactional
    public WishlistStatus toggleWishlist(Long memberNo, Long shopNo) {

        /* 1. 회원 엔티티 조회 (존재하지 않으면 예외)  */
        MemberEntity member = memberRepository.findById(memberNo) .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        /* 2. 회원 + 상점 기준 찜 이력 조회 (ACTIVE / DELETED 상관없이)  */
        Optional<WishlistEntity> optionalWish = wishlistRepository.findMyWish(memberNo, shopNo);

        /* 3. 찜 이력이 없는 경우 → 새로 생성  */
        if (optionalWish.isEmpty()) {

            WishlistEntity newWish = new WishlistEntity();
            newWish.setMember(member);

            /* ShopEntity는 ID만 가진 프록시 객체로 세팅 (불필요한 DB 조회 방지)  */
            ShopEntity shop = new ShopEntity();
            shop.setShopNo(shopNo);

            newWish.setShop(shop);
            newWish.setStatus(WishlistStatus.ACTIVE);

            wishlistRepository.save(newWish);

            return WishlistStatus.ACTIVE;
        }

        /* 4. 기존 찜 상태 토글  */
        WishlistEntity wish = optionalWish.get();

        if (wish.getStatus() == WishlistStatus.ACTIVE) {
            wish.setStatus(WishlistStatus.DELETED);
            return WishlistStatus.DELETED;
        }

        // DELETED → ACTIVE
        wish.setStatus(WishlistStatus.ACTIVE);
        return WishlistStatus.ACTIVE;
    }
}