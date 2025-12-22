package com.eob.main.model.service;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainService {

    private final ShopRepository shopRepository;

    // 상점내역 반환
    public Page<ShopEntity> getShopList(String category, ArrayList<Double> location, Pageable pageable) {
        // try {
        // 사용자 위치 기준 검색 범위 계산 -> 최소/최대 위도, 최소/최대 경도
        double radiusKm = radiusKm; // 검색 반경
        double lat = location.get(0);
        double lng = location.get(1);

        double latRange = radiusKm / 111.0;
        double lngRange = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));

        double minLat = lat - latRange;
        double maxLat = lat + latRange;
        double minLng = lng - lngRange;
        double maxLng = lng + lngRange;
        // 위치, 카테고리에 맞는 상점 조회
        Page<ShopEntity> shopList = shopRepository.findByProductCatNameAndLocation(category, minLat, maxLat, minLng,
                maxLng, pageable);
        return shopList;
        // } catch (Exception e) {
        // return e.getMessage();
        // }
    }

    //
}
