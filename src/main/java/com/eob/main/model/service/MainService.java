package com.eob.main.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainService {

    private final ShopRepository shopRepository;

    // 상점내역 반환
    public Page<ShopEntity> getShopList(String category, Pageable pageable) {
        // try {
        // 위치, 카테고리에 맞는 상점 조회
        // 카테고리 조회 우선-> 위치 추가 시 지우기
        Page<ShopEntity> shopList = shopRepository.findByProductCatName(category,
                pageable);
        return shopList;
        // } catch (Exception e) {
        // return e.getMessage();
        // }
    }
}
