package com.eob.shop.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PRODUCT_FILE")
@Getter
@Setter
public class ProductFileEntity {

    /**
     * 상품 파일 고유 번호 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_file_seq")
    @SequenceGenerator(name = "product_file_seq", sequenceName = "PRODUCT_FILE_SEQ", allocationSize = 1)
    @Column(name = "PRODUCT_FILE_NO")
    private Long productFileNo;

    /**
     * 상품 정보 FK
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_NO", nullable = false)
    private ProductEntity product;

    /**
     * 파일명 또는 파일 경로
     */
    @Column(name = "PRODUCT_FILE", nullable = false, length = 4000)
    private String productFile;

    /**
     * 파일 생성 일시
     */
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}