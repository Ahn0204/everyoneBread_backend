package com.eob.order.model.data;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderForm { // 주문 유효성 검사&dto 겸용

    private String cart; // 주문 상품 내역-> 뷰로부터 JSON문자열로 받고, 컨트롤러에서 mapper로 변환

    private int productPrice; // 상품 총액

    @NotNull
    private int orderPrice; // 총 결제 금액

    @NotNull
    private long buyerMemberNo; // 구매자 번호

    @NotNull
    private long shopNo; // 상점 번호

    @NotNull
    private int deliveryFee; // 배송비

    @NotNull(message = "받을 주소를 입력해주세요.")
    private String orderAddress; // 배송지

    // @NotNull(message = "받는 사람을 입력해주세요.")
    // private String orderName; // 받는 사람 이름

    @NotNull(message = "받는 사람 연락처를 입력해주세요.")
    private String orderPhone; // 받는 사람 번호

    private String orderRequest; // 가게 요청사항

    private String riderRequest; // 라이더 요청사항

    @NotNull
    private long merchantUid; // 결제된 주문 식별 번호 => orderNo로 사용

    // @OneToOne(mappedBy = "orderTime", cascade = CascadeType.REMOVE ) //orderTime
    // 주문시간 테이블 참조
    // private OrderTimeDTO orderTime; // 주문 시간 테이블

    private int deliveryCycle; // 배송주기 >> DB 입력 시 LocalDateTime객체로 입력

    private String customerUid; // 정기배송 결제정보 저장용 키

    // private String name; // 주문명: '테스트결제'로 일괄 입력됨

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime nextPaymentAt; // 다음 결제일 (결제일 + 31일)

    private String payAlert; // 정기결제 전 알림 여부

}
