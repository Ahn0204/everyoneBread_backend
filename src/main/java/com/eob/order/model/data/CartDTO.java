package com.eob.order.model.data;

import lombok.Data;

@Data
public class CartDTO { // 주문 상세내역에 insert할 때 사용하는 장바구니 내역용 DTO

    private long productNo; // 상품 번호

    private String productName; // 상품명

    private int productPrice; // 상품 가격

    private int quantity; // 수량
}
