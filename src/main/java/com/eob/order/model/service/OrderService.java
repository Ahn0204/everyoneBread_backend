package com.eob.order.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eob.common.websocket.shop.dto.ShopOrderSocketMessage;
import com.eob.common.websocket.shop.sender.ShopSocketSender;
import com.eob.alert.model.service.AlertService;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.repository.MemberRepository;
import com.eob.order.model.data.CartDTO;
import com.eob.order.model.data.OrderDetailEntity;
import com.eob.order.model.data.OrderForm;
import com.eob.order.model.data.OrderHistoryEntity;
import com.eob.order.model.data.OrderStatus;
import com.eob.order.model.data.OrderTimeEntity;
import com.eob.order.model.repository.OrderDetailRepository;
import com.eob.order.model.repository.OrderHistoryRepository;
import com.eob.shop.model.data.ProductEntity;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.repository.ProductRepository;
import com.eob.shop.repository.ShopRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderHistoryRepository orderHistoryRepository;
    private final PortOneService portOneService;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final AlertService alertService;
    private final SimpMessagingTemplate messagingTemplat; // STOMP 메세지 발송 전용 객체

    // 판매자에게 실시간 알림을 보내기 위한 WebSocket Sender
    private final ShopSocketSender shopSocketSender;

    /**
     * [판매자]
     * 판매자 주문 관리 페이지에서 사용하는 주문 조회 메서드
     * - 기간 필터 화면에서만 적용
     * - ShopOrderController에서 사용
     */
    @Transactional(readOnly = true)
    public List<OrderHistoryEntity> findByShopNo(Long shopNo) {
        return orderHistoryRepository.findByShop_ShopNo(shopNo);
    }

    /**
     * [판매자]
     * 판매자 주문 관리 페이지에서 사용하는 상태별 주문 조회 메서드
     * - 상품 상태(status) 필터 화면
     * - 전체/대기중/배송중/배송완료/취소 등 상태별 조회
     * - ShopOrderController에서 사용
     */
    @Transactional(readOnly = true)
    public List<OrderHistoryEntity> findByShopNoAndStatus(Long shopNo, OrderStatus status) {
        return orderHistoryRepository.findByShop_ShopNoAndStatus(shopNo, status);
    }

    /**
     * [판매자]
     * 주문 수락 처리 메서드
     * - ORDER -> ASSIGN 상태로 변경
     */
    @Transactional
    public void acceptOrder(Long orderNo) {
        // 주문 조회
        OrderHistoryEntity order = orderHistoryRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다.: "));

        // 현재 상태가 ORDER인 경우에만 PREPARE로 변경
        if (order.getStatus() != OrderStatus.ORDER) {
            throw new IllegalStateException("대기 상태의 주문만 수락할 수 있습니다.");
        }

        // 상태 변경
        order.setStatus(OrderStatus.REQUEST);

        // WebSocket을 통한 실시간 알림 전송
        // 추가 시각: 2026/01/09
        /**
         * 판매자에게 실시간 주문 알림 보내기
         * ShopOrderSocketMessage : 판매자에게 보낼 웹소켓 메시지 데이터 상자
         */
        ShopOrderSocketMessage socketMessage = new ShopOrderSocketMessage(
            order.getOrderNo(),                   // 주문 번호
            order.getShop().getShopNo(),          // 가게 번호
            "새로운 주문이 들어왔습니다!",  // 판매자에게 보여줄 메시지
            order.getStatus().name()              // 주문 상태
        );

        // ShopSocketSender를 통해 해당 가게(shopNo)를 구독 중인 판매자 브라우저들에게 실시간으로 메시지 전송
        shopSocketSender.sendNewOrder(
            order.getShop().getShopNo(), socketMessage
        );
        order.setStatus(OrderStatus.REQUEST);
    }

    /**
     * [판매자]
     * 주문 거절 처리 메서드
     * - ORDER -> REJECT 상태로 변경
     * - 거절 사유 저장
     */
    @Transactional
    public void rejectOrder(Long orderNo, String reason) {

        // 1. 주문 조회
        OrderHistoryEntity order = orderHistoryRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 2. 상태 검증 (ORDER만 가능)
        if (order.getStatus() != OrderStatus.ORDER) {
            throw new IllegalStateException("대기 상태의 주문만 거절할 수 있습니다.");
        }

        // 3. 거절 처리
        order.setStatus(OrderStatus.REJECT);
        order.setRejectReason(reason);
    }

    /**
     * [판매자]
     * 오늘 주문 수 조회
     */
    @Transactional(readOnly = true)
    public long countTodayOrders(Long shopNo) {
        // 오늘 날짜의 시작과 끝 시간 계산
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        // 오늘 날짜의 끝 시간은 내일 00:00:00에서 1나노초 뺀 시간
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        // 조회
        return orderHistoryRepository.countTodayOrders(shopNo, start, end);
    }

    /**
     * [판매자]
     * 상태별 주문 수 조회
     */
    @Transactional(readOnly = true)
    public long countByStatus(Long shopNo, OrderStatus status) {
        return orderHistoryRepository.countByShop_ShopNoAndStatus(shopNo, status);
    }

    // 단일 주문 조회
    @Transactional(readOnly = true)
    public OrderHistoryEntity findById(Long orderNo) {
        return orderHistoryRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
    }

    // ==============================================[ order
    // ]===============================================

    /**
     * 결제완료 후 주문 내역 기록
     * 매개변수: orderForm
     */
    @Transactional
    public void insertOrder(OrderForm orderForm) {
        // 문제발생 시 결제 취소용
        String token = portOneService.getToken();

        // 1. 주문 내역, 주문 시간 내역 insert 2.주문 상세 내역 insert 3.판매자에게 알림 insert,웹소켓으로 메세지전송

        // 1. 주문내역, 주문 시간 내역 insert
        // 주문 내역 엔티티 생성
        OrderHistoryEntity order = new OrderHistoryEntity();
        // Member엔티티 조회
        MemberEntity member = memberRepository.findByMemberNo(orderForm.getBuyerMemberNo());
        if (member == null) {
            throw new IllegalArgumentException("회원 정보가 존재하지 않습니다.");
        }
        // Shop엔티티 조회
        ShopEntity shop = shopRepository.findByShopNo(orderForm.getShopNo());
        if (shop == null) {
            throw new IllegalArgumentException("상점 정보가 존재하지 않습니다.");
        }

        // 영업 가능 여부 체크 (휴무 / 영업 종료 시 예외 발생)
        // 영업일 입력 가능해지면 주석해제
        // shopService.validateShopOrderable(shop);

        // orderForm에서 값 꺼내 order에 set
        order.setMember(member); // 구매자 정보
        order.setDeliveryFee(orderForm.getDeliveryFee());
        order.setOrderNo(orderForm.getMerchantUid());
        order.setOrderAddress(orderForm.getOrderAddress());
        order.setOrderPrice(orderForm.getOrderPrice());
        order.setOrderPhone(orderForm.getOrderPhone());
        order.setOrderRequest(orderForm.getOrderRequest());
        order.setRiderRequest(orderForm.getRiderRequest());
        order.setShop(shop); // 판매자(가게) 정보

        // 주문 시간 엔티티 생성
        OrderTimeEntity orderTime = new OrderTimeEntity();
        orderTime.setOrderedAt(LocalDateTime.now()); // 주문 insert되는 시간
        orderTime.setOrder(order); // order저장 시 동시에 insert되도록
        // orderTimeDTO를 order에 set
        order.setOrderTime(orderTime); // orderHistoryEntity의 컬럼값에 cascadeType.ALL 설정 => 주문 내역 저장 시 시간 엔티티가 동시 저장됨

        // 1에서 insert된 주문 내역의 order가져오기-> orderNo로 넣은 merchantUid 사용
        OrderHistoryEntity ordered = orderHistoryRepository.save(order); // cascadeType.ALL 설정 => 주문 내역&주문시간 엔티티 동시 저장 +
                                                                         // 방금 저장한 주문내역 엔티티를 변수에 담기
        // 2.주문 상세 내역 insert
        // System.out.println("장바구니 문자열 출력:" + orderForm.getCart());
        // 장바구니 내역에서 주문 상세에 넣을 상품 정보 조회
        ObjectMapper mapper = new ObjectMapper();
        List<CartDTO> cartList;
        try {
            cartList = mapper.readValue(orderForm.getCart(), new TypeReference<List<CartDTO>>() {
            });
            for (CartDTO c : cartList) { // 결제된 상품 1개씩 꺼내기
                // CartDTO c = (CartDTO) cart;
                // 주문 상세 내역 엔티티 생성
                OrderDetailEntity orderDetail = new OrderDetailEntity(); // orderDetail에 상품 정보 set
                orderDetail.setOrderNo(ordered); // 주문 객체 넣기
                // 상품 객체 가져오기
                Optional<ProductEntity> _product = productRepository.findById(c.getProductNo());
                if (_product.isEmpty()) { // 조회되는 상품이 없다면
                    portOneService.getRefund(token, orderForm.getMerchantUid()); // 결제 취소
                    throw new IllegalStateException("상품이 존재하지 않음");
                }
                ProductEntity product = _product.get();
                orderDetail.setProductNo(product); // 상품
                orderDetail.setPrice(c.getProductPrice()); // 가격
                orderDetail.setQuantity(c.getQuantity()); // 수량
                orderDetail.setCreatedAt(LocalDateTime.now()); // insert일시
                orderDetailRepository.save(orderDetail); // 주문 상세 내역 insert
            } // 결제한 장바구니의 상품 1개 꺼내는 반복문 종료
        } catch (Exception e) {
            portOneService.getRefund(token, orderForm.getMerchantUid()); // 결제 취소
            throw new RuntimeException("장바구니 JSON 파싱 실패", e);
            // e.printStackTrace();
        }

        // 생성된 List<상세내역>를 주문내역에 저장
        ordered.setOrderDetail(orderDetailRepository.findByOrderNo_OrderNo(ordered.getOrderNo()));
        orderHistoryRepository.save(ordered);

        // 3. 판매자에게 알림 insert,웹소켓으로 메세지전송
        // 판매자DB에 알림 INSERT
        MemberEntity shopMember = shop.getMember();
        // (보내는멤버Entity, 받는멤버No, 대분류String, 소분류String)
        // =(구매자memberEntity, 상점memberNo, 타입:주문 관련, 타입코드:주문 추가)
        alertService.sendAlert(member, shopMember.getMemberNo(), "ORDER", "INSERTED");
        // 판매자 뷰에 알림 출력시키기
        String shopId = shopMember.getMemberId();
        messagingTemplat.convertAndSendToUser(shopId, "/to/order", "주문이 추가되었습니다.");

    }

}