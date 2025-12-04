package com.eob.shop.repository;

import com.eob.shop.model.data.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * ShopEntity(shopNo) 기준으로 상품 목록 조회
     * - JPA 규칙에 따라 shop.shopNo 로 접근하는 메서드명
     */
    List<ProductEntity> findByShop_ShopNo(Long shopNo);

}
