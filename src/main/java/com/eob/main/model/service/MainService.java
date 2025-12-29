package com.eob.main.model.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eob.admin.model.data.DistanceFeeEntity;
import com.eob.admin.model.repository.DistanceFeeRepository;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainService {

    private final ShopRepository shopRepository;
    public final DistanceFeeRepository distanceFeeRepository;

    // 상점내역 반환
    public Page<ShopEntity> getShopList(String category, Map<String, Object> data, Pageable pageable) {
        Page<ShopEntity> shopList = null;

        // center좌표 값 data에서 꺼내기
        double lat = (double) data.get("lat"); // 위도
        double lng = (double) data.get("lng"); // 경도
        int radiusKm = (int) data.get("radiusKm"); // 반경km
        // 사용자 위치 기준 검색 범위 계산 -> 최소/최대 위도, 최소/최대 경도
        double latRange = radiusKm / 111.0;
        double lngRange = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));

        double minLat = lat - latRange;
        double maxLat = lat + latRange;
        double minLng = lng - lngRange;
        double maxLng = lng + lngRange;
        // 위치, 카테고리에 맞는 상점 조회
        shopList = shopRepository.findNearBy(category, lat, lng, minLat, maxLat,
                minLng,
                maxLng, pageable);

        // 상점과 사용자간 직선거리 구하고 shop엔티티에 저장
        String d = null;
        for (ShopEntity shop : shopList) {
            double distance = haversine(lat, lng, shop.getLatitude(), shop.getLongitude());
            if (distance < 1) { // 거리가 1km미만이라면
                d = (int) (distance * 1000 / 10) * 10 + "m"; // m로 환산
            } else { // 거리가 1km이상이라면
                d = Math.floor(distance * 10) / 10 + "km"; // km로 환산
            }
            shop.setDistance(d); // distance저장
        }
        return shopList;
    }

    // 직선거리 구하기 -> lat1,lng2= 사용자 위치 / lat2,lng2=상점 위치
    public double haversine(
            double lat1, double lng1,
            double lat2, double lng2) {

        final double R = 6371.0; // 지구 반지름(km)

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    // 거리에 해당하는 배송비 꺼내기
    public int getDeliveryFeeByDistance(int distance) {
        int deliveryFee = -1;
        Optional<DistanceFeeEntity> _distanceFee = distanceFeeRepository.findByDistance(distance);
        if (!_distanceFee.isEmpty()) {
            // 값이 있다면
            DistanceFeeEntity distanceFee = _distanceFee.get();
            deliveryFee = distanceFee.getDeliveryFee();
        }
        return deliveryFee;
    }
}
