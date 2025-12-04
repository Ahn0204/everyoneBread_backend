package com.eob.shop.service;

import com.eob.shop.model.data.ProductStatus;
import com.eob.shop.model.data.ProductEntity;
import com.eob.shop.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 상품 관련 비즈니스 로직을 처리하는 서비스 클래스
 * - 상품 조회(목록/단건)
 * - 상품 저장(등록 /수정)
 * - 상품 삭제(상태 변경)
 *
 * 컨트롤러에서 직접 Repository를 호출하지 않고,
 * 반드시 Service를 통해 비즈니스 로직을 수행하도록 설계한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    // ProductEntity DB 접근을 위한 Repository
    private final ProductRepository productRepository;


    /**
     * 특정 상점(shopNo)의 전체 상품 목록 조회
     * - ProductController#list() 에서 호출됨
     * - "/shop/products" 페이지에서 상품 목록을 보여줄 때 사용
     *
     * @param shopNo 상점 고유 번호 (FK)
     * @return 해당 상점의 모든 상품 리스트
     */
    public List<ProductEntity> findByShopNo(Long shopNo) {
        return productRepository.findByShop_ShopNo(shopNo);
    }


    /**
     * 상품 단건 조회
     * - 상품 수정 페이지에서 기존 데이터를 불러오기 위해 사용
     * - ProductController#editForm() 에서 호출됨
     *
     * @param productNo 상품 고유 번호(PK)
     * @return 조회된 상품 엔티티
     */
    public ProductEntity findById(Long productNo) {
        return productRepository.findById(productNo)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }


    /**
     * 상품 저장(등록 또는 수정)
     * - ProductController#addProduct()
     * - ProductController#editProduct()
     * 에서 공통으로 사용됨
     *
     * JPA save()는
     *  1) productNo가 null → INSERT
     *  2) productNo가 존재 → UPDATE
     *
     * @param product 저장할 상품 엔티티
     * @return 저장된 상품 엔티티
     */
    public ProductEntity save(ProductEntity product) {
        return productRepository.save(product);
    }


    /**
     * 상품 삭제 처리
     * - 실제 DB에서 삭제하는 것이 아니라
     *   상태를 DELETED 로 변경하여 “비노출” 처리
     * - ProductController#delete() 에서 호출됨
     *
     * @param productNo 삭제할 상품 번호
     */
    public void delete(Long productNo) {
        ProductEntity product = findById(productNo); // 존재 여부 확인
        product.setStatus(ProductStatus.DELETED);    // 상태 변경 (soft delete)
    }
}
