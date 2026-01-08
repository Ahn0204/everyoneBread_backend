package com.eob.member.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eob.member.model.data.ReviewEntity;
import com.eob.member.model.data.ReviewStatus;
import com.eob.member.model.dto.MyOrderDTO;
import com.eob.member.model.dto.ReviewListResponse;
import com.eob.member.repository.ReviewRepository;
import com.eob.order.model.data.OrderDetailResponse;
import com.eob.order.model.data.OrderHistoryEntity;
import com.eob.order.model.data.OrderItemDTO;
import com.eob.order.model.data.OrderStatus;
import com.eob.order.model.repository.OrderHistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * 회원의 마이페이지 관련 서비스
 * - 데이터 조회
 * - 주문, 즐겨찾기, 배송지, 리뷰 등 "마이페이지 전용 로직"을 책임
 * 
 * 주의
 * - Repository는 도메인(Order)에 맞는 OrderHistoryRepository를 사용
 * - Entity를 그대로 넘기지 않고 DTO로 변환해서 Controller에 전달
 */
@Service
@RequiredArgsConstructor
public class MypageService {

    private final OrderHistoryRepository orderHistoryRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 주문 내역 목록 조회
     */
    /**
     * 사용 페이지 : member/mypage/orderList.html
     * 
     * 기능 설명
     * - 로그인한 회원(memberNo)의 주문 이력을 DB에서 조회함.
     * - 주문 엔티티(OrderHistoryEntity)를 마이페이지 화면용 DTO(MyOrderDTO)로 변환함.
     * - 화면에 필요한 데이터만 DTO에 담아서 변환
     * 
     * 구현 범위
     * - 주문 번호, 상점명, 총 결제 금액, 주문 상태 주문 시간
     * 
     * 다음 단계 : 대표 상품명, 상품 개수, 썸네일
     * 
     * @param memberNo 로그인한 회원 번호
     * @return 회원 주문 목록 (마이페이지 출력용)
     */
    public List<MyOrderDTO> getMyOrders(Long memberNo){

        // 회원 기준 주문 이력 조회 (최신순)
        List<OrderHistoryEntity> orders = orderHistoryRepository.findMyOrders(memberNo);

        // Entity -> DTO 변환
        return orders.stream()
            .map(order -> {
                MyOrderDTO dto = new MyOrderDTO();

                // 주문 번호
                dto.setOrderNo(order.getOrderNo());
                // 상점명
                dto.setShopName(order.getShop().getShopName());
                // 총 결제 금액
                dto.setOrderPrice(order.getOrderPrice());
                // 주문 상태 (WAIT, PREPARE, COMPLETE 등)
                dto.setStatus(order.getStatus());
                // 주문 시간
                // OrderTimeEntity 내부의 orderedAt 사용
                if(order.getOrderTime() != null){
                    dto.setOrderTime(order.getOrderTime().getOrderedAt());
                }
                // 대표 상품 이름 dto.setMainProductName()
                // 상품 카운트 dto.setProductCount()
                // 썸네일 dto.setThumbnail()

                return dto;
            }).toList();
    }

    /**
     * [마이페이지 - 주문 상세 조회]
     *
     * 사용 위치
     * - MemberController → /member/mypage/orderDetail
     *
     * 처리 흐름
     * 1. 주문 번호로 주문 조회
     * 2. OrderEntity → OrderDetailResponse DTO 변환
     * 3. Controller로 DTO 반환
     */
    public OrderDetailResponse getOrderDetail(Long orderNo) {

        OrderHistoryEntity order =
            orderHistoryRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));

        List<OrderItemDTO> items = order.getOrderDetail().stream()
            .map(detail -> new OrderItemDTO(
                detail.getProductNo().getProductName(),
                detail.getQuantity(),
                detail.getPrice()
            ))
            .toList();

        return new OrderDetailResponse(
            order.getOrderNo(),
            order.getStatus(),
            order.getOrderPrice(),
            order.getDeliveryFee(),
            order.getOrderAddress(),
            items
        );
    }

    /**
     * [마이페이지 - 주문 취소]
     *
     * 처리 규칙
     * - 본인 주문만 취소 가능
     * - 상태가 WAIT(대기)인 주문만 취소 가능
     * - 취소 시 상태를 CANCEL 또는 REJECT로 변경
     */
    @Transactional
    public void cancelOrder(Long orderNo, Long memberNo) {
        OrderHistoryEntity order =
            orderHistoryRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new IllegalArgumentException("주문 없음"));

        if (order.getStatus() != OrderStatus.WAIT) {
            throw new IllegalStateException("취소 불가 상태");
        }

        order.cancel(); // status = REJECT or CANCEL
    }


    /**
     * [마이페이지 - 내 후기 목록 조회]
     *
     * 사용 위치
     * - MemberController → /member/mypage/reviewList
     *
     * 처리 흐름
     * 1. 로그인 회원 번호로 리뷰 조회 (POSTED 상태만)
     * 2. ReviewEntity → ReviewListResponse DTO 변환
     * 3. Controller로 DTO 리스트 반환
     */
    public List<ReviewListResponse> getMyReviews(Long memberNo) {

        // 1️. 리뷰 엔티티 조회 (POSTED 상태만)
        List<ReviewEntity> reviews =
                reviewRepository.findMyReviews(memberNo, ReviewStatus.POSTED);

        // 2️. Entity → DTO 변환
        return reviews.stream()
            .map(review -> {

                ReviewListResponse dto = new ReviewListResponse();

                dto.setReviewNo(review.getReviewNo());
                dto.setContent(review.getContent());
                dto.setCreatedAt(review.getCreatedAt());

                // 별점 (Integer → Double 변환)
                dto.setRating(
                    review.getRating() != null
                        ? review.getRating().doubleValue()
                        : 0.0
                );

                // 주문 정보
                dto.setOrderNo(review.getOrder().getOrderNo());

                /**
                 * 상품 정보 주의
                 * - Order ↔ Product 관계에 따라 접근 경로가 다를 수 있음
                 * - 아래는 "주문에 상품이 여러 개 있는 구조" 기준 예시
                 */
                if (review.getOrder().getOrderDetail() != null
                    && !review.getOrder().getOrderDetail().isEmpty()) {
                    var orderDetail = review.getOrder().getOrderDetail().get(0);
                    dto.setProductName(
                        orderDetail.getProductNo().getProductName()
                    );
                    dto.setProductImg(
                        orderDetail.getProductNo().getImgUrl()
                    );
                }

                return dto;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * [마이페이지 - 내 후기 목록 페이징 조회]
     *
     * @param memberNo 로그인 회원 번호
     * @param page     페이지 번호 (0부터 시작)
     */
    public Page<ReviewListResponse> getMyReviews(Long memberNo, int page) {

        // 1️. 페이지 요청 객체 생성
        PageRequest pageable =
            PageRequest.of(page, 5, Sort.by("createdAt").descending());

        // 2️. 페이징 조회
        Page<ReviewEntity> reviewPage =
            reviewRepository.findMyReviews(memberNo, ReviewStatus.POSTED, pageable);

        // 3️. Entity → DTO 변환 (Page.map 사용)
        return reviewPage.map(this::convertToReviewDto);
    }    
    
    /**
     * [마이페이지 - 후기 삭제]
     *
     * 처리 규칙
     * - 실제 삭제 X
     * - 상태값을 DELETED 로 변경
     * - 본인 리뷰만 삭제 가능
     */
    @Transactional
    public void deleteMyReview(Long reviewNo, Long memberNo) {

        // 1️. 리뷰 단건 조회
        ReviewEntity review = reviewRepository.findByReviewNo(reviewNo)
            .orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 리뷰입니다.")
            );

        // 2️. 본인 리뷰인지 검증 (보안 핵심)
        if (!review.getMember().getMemberNo().equals(memberNo)) {
            throw new SecurityException("본인의 리뷰만 삭제할 수 있습니다.");
        }

        // 3️. 상태 변경 (soft delete)
        review.setStatus(ReviewStatus.DELETED);

        // 4️. save 호출 X
        // @Transactional + dirty checking 으로 자동 UPDATE
    }

    /**
     * ReviewEntity → ReviewListResponse 변환
     * (중복 제거용 공통 메서드)
     */
    private ReviewListResponse convertToReviewDto(ReviewEntity review) {

        ReviewListResponse dto = new ReviewListResponse();

        dto.setReviewNo(review.getReviewNo());
        dto.setContent(review.getContent());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setOrderNo(review.getOrder().getOrderNo());

        // 별점 (Integer → Double 변환)
        dto.setRating(
            review.getRating() != null
                ? review.getRating().doubleValue()
                : 0.0
        );        

        if (review.getOrder().getOrderDetail() != null
            && !review.getOrder().getOrderDetail().isEmpty()) {

            var orderDetail = review.getOrder().getOrderDetail().get(0);

            dto.setProductName(
                orderDetail.getProductNo().getProductName()
            );

            dto.setProductImg(
                orderDetail.getProductNo().getImgUrl()
            );
        }

        return dto;
    }
}