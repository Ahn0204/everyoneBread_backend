package com.eob.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eob.member.model.data.ReviewEntity;
import com.eob.member.model.data.ReviewStatus;

/**
 * [Repository]
 * - REVIEW 테이블 전용 Repository
 * - 리뷰 조회 / 수정 / 삭제(soft delete) 담당
 */
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    /**
     * [마이페이지 - 리뷰 목록 조회용]
     *
     * Controller → Service → 이 메서드 호출
     *
     * 조회 조건
     * - 특정 회원(memberNo)의
     * - 특정 상태(POSTED) 리뷰만
     * - 최신순 정렬
     */
    @Query("select r from ReviewEntity r where r.member.memberNo = :memberNo and r.status = :status order by r.createdAt desc")
    List<ReviewEntity> findMyReviews(@Param("memberNo") Long memberNo, @Param("status") ReviewStatus status );

    /**
     * [리뷰 단건 조회 - 삭제용]
     * - reviewNo 기준
     */
    Optional<ReviewEntity> findByReviewNo(Long reviewNo);

    /**
     * 후기 목록 페이징 조회
     */
    @Query("select r from ReviewEntity r where r.member.memberNo = :memberNo and r.status = :status")
    Page<ReviewEntity> findMyReviews( @Param("memberNo") Long memberNo,  @Param("status") ReviewStatus status, Pageable pageable );
}