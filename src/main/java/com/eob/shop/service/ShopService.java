package com.eob.shop.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

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

    /**
     * 상점 마이페이지 - 상점 정보 수정
     */
    public void updateShopInfo(Long memberNo, String type, String value) {

        ShopEntity shop = shopRepository.findByMember_MemberNo(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("상점 정보를 찾을 수 없습니다."));

        switch (type) {
            case "name" -> shop.setShopName(value);
            case "intro" -> shop.setShopIntro(value);
            case "address" -> shop.setShopAddress(value);
            case "holiday" -> shop.setHoliday(value);
            case "time" -> {
                String[] t = value.split("~");
                shop.setOpenTime(t[0].trim());
                shop.setCloseTime(t[1].trim());
            }
        }
    }

    /**
     * 상점 영업 상태 반환
     * "영업중", "영업종료", "휴무"
     */
    public String getShopOpenStatus(ShopEntity shop) {

        // 1. 휴무일 체크
        if (shop.getHoliday() != null && !shop.getHoliday().isBlank()) {
            String today = LocalDate.now()
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.KOREAN); // 월, 화, 수 …

            if (shop.getHoliday().contains(today)) {
                return "휴무";
            }
        }

        // 2. 영업시간 체크
        if (shop.getOpenTime() != null && shop.getCloseTime() != null) {
            LocalTime now = LocalTime.now();
            LocalTime open = LocalTime.parse(shop.getOpenTime());
            LocalTime close = LocalTime.parse(shop.getCloseTime());

            if (now.isAfter(open) && now.isBefore(close)) {
                return "영업중";
            }
        }
        return "영업종료";
    }
    /**
     * 주문 가능 여부 체크
     * @throws IllegalStateException 주문 불가 사유
     */
    public void validateShopOrderable(ShopEntity shop) {

        String status = getShopOpenStatus(shop);

        if ("휴무".equals(status)) {
            throw new IllegalStateException("해당 상점은 현재 휴무일입니다.");
        }

        if ("영업종료".equals(status)) {
            throw new IllegalStateException("현재 영업 시간이 아닙니다.");
        }
    }

}