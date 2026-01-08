package com.eob.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eob.shop.model.data.ProductEntity;
import com.eob.shop.model.data.ProductStatus;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // 상품 관리 페이지용 (삭제된 상품 제외)
    Page<ProductEntity> findByShop_ShopNoAndStatusNot(Long shopNo, ProductStatus status, Pageable pageable);

    // 특정 상점(shopNo)의 상품 목록 리스트 조회
    List<ProductEntity> findByShop_ShopNoAndStatusNot(Long shopNo, ProductStatus status);

    ProductEntity findByProductNo(ProductEntity productNo);

}