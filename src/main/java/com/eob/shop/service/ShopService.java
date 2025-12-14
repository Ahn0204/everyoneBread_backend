package com.eob.shop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;

    /**
     * 상점 저장
     */
    public ShopEntity saveShop(ShopEntity shop) {
        return shopRepository.save(shop);
    }

    /**
     * 상점명 중복 확인
     * return true = 이미 존재
     * return false = 사용 가능
     */
    public boolean existsByShopName(String shopName) {
        return shopRepository.existsByShopName(shopName);
    }

    /**
     * 특정 회원 번호로 상점 조회 (추후 MyPage, 상품등록 시 필요한 기능)
     */
    public ShopEntity findByMemberNo(Long memberNo) {
        return shopRepository.findByMember_MemberNo(memberNo)
                .orElse(null);
    }

    /**
     * 상점 번호로 조회
     */
    public ShopEntity findByShopNo(Long shopNo) {
        return shopRepository.findById(shopNo)
                .orElseThrow(() -> new IllegalArgumentException("상점 정보를 찾을 수 없습니다."));
    }

}
