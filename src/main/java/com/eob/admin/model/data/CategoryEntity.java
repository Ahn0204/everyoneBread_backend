package com.eob.admin.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "category")
public class CategoryEntity {

    /**
     * 카테고리 고유번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", sequenceName = "category_seq", allocationSize = 1)
    private long categoryNo;

    /**
     * 소분류일 경우, 해당하는 대분류의 categoryNo
     */
    private long parentNo;

    /**
     * 카테고리명
     */
    private String category;

    /**
     * 계층 0=대분류, 1=소분류
     */
    private int depth;

    /**
     * 상태 0=정상, 1=숨김(관리자 삭제)
     */
    private int status;

    /**
     * 갱신 일시 관리자 update&insert 일시(LocalDateTime.now())
     */
    private LocalDateTime updatedAt;
}
