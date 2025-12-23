package com.eob.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eob.member.model.dto.MyOrderDTO;
import com.eob.order.model.data.OrderHistoryEntity;
import com.eob.order.model.repository.OrderHistoryRepository;

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

}