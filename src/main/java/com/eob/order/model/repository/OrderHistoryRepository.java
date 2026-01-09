package com.eob.order.model.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eob.member.model.data.MemberEntity;
import com.eob.order.model.data.OrderHistoryEntity;
import com.eob.order.model.data.OrderStatus;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, Long> {

    /**
     * [판매자]
     * - 판매자 주문 관리 페이지에서 사용
     * - 상점 기준 주문 전체 조회만 담당
     * 
     * @param shopNo 상점 고유 번호
     * @return 상점의 모든 주문 내역 리스트
     */
    List<OrderHistoryEntity> findByShop_ShopNo(Long shopNo);

    /**
     * [판매자]
     * - 판매자 주문 관리 페이지에서 사용
     * - 상태별 주문 조회 담당
     * 
     * @param shopNo 상점 고유 번호
     * @param status 주문 상태 코드
     * @return 상점의 특정 상태 주문 내역 리스트
     */
    List<OrderHistoryEntity> findByShop_ShopNoAndStatus(Long shopNo, OrderStatus status);

    /**
     * [판매자]
     * 오늘 주문 수 조회
     */
    @Query("select count(o) from OrderHistoryEntity o where o.shop.shopNo = :shopNo and o.orderTime is not null and o.orderTime.orderedAt between :start and :end")
    long countTodayOrders(@Param("shopNo") Long shopNo, @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * [판매자]
     * 상태별 주문 수 조회
     */
    long countByShop_ShopNoAndStatus(Long shopNo, OrderStatus status);

    // [라이더]
    List<OrderHistoryEntity> findTop10ByStatusInOrderByOrderTimeOrderedAtDesc(List<OrderStatus> asList);

    List<OrderHistoryEntity> findTop10ByStatusOrderByOrderTimeOrderedAtDesc(OrderStatus request);

    List<OrderHistoryEntity> findByStatusAndRiderOrderByOrderTimeOrderedAtDesc(OrderStatus pickup,
            MemberEntity memberEntity);

    List<OrderHistoryEntity> findTop10ByStatusAndRiderOrderByOrderTimeOrderedAtDesc(OrderStatus complete,
            MemberEntity memberEntity);

    // ORDER_NO로 해당 주문 내역 가져오기
    Optional<OrderHistoryEntity> findByOrderNo(Long orderNo);

    /**
     * [회원]
     * - 회원 마이페이지 주문 내역 조회
     * - 회원 기준 주문 전체 조회 (최신순)
     * 
     * @param memberNo 회원 고유 번호
     * @return 회원의 주문 내역 리스트
     */
    @Query("select o from OrderHistoryEntity o where o.member.memberNo = :memberNo order by o.orderTime.orderedAt desc")
    List<OrderHistoryEntity> findMyOrders(@Param("memberNo") Long memberNo);

    /**
     * [판매자]
     * 상점별 전체 주문 금액 합계 조회 (REJECT 상태 제외)
     */
    @Query("select coalesce(sum(o.orderPrice), 0) from OrderHistoryEntity o where o.shop.shopNo = :shopNo and o.status != 'REJECT'")
    long sumOrderPriceByShop(@Param("shopNo") Long shopNo);

    /**
     * 주문 번호로 주문 조회
     */
    // Optional<OrderHistoryEntity> findByOrderNo(Long orderNo);
}