package com.eob.admin.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eob.admin.model.data.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    // 조건에 맞는 categoryEntity객체를 리턴함..?

    /**
     * 대분류 카테고리 조회(depth=0)
     * 메인Category(리스트) 조회
     */
    @Query("SELECT category FROM CategoryEntity WHERE depth = :depth ORDER BY updatedAt")
    Optional<ArrayList<String>> findByDepth(@Param("depth") int depth);
}
