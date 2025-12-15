package com.eob.shop.repository;

import com.eob.shop.model.data.ProductEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // 특정 상점(shopNo)의 상품 목록 페이징 조회
    Page<ProductEntity> findByShop_ShopNo(Long shopNo, Pageable pageable);

}