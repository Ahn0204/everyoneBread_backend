package com.eob.rider.model.data;

import java.time.LocalDateTime;
import java.util.List;

import com.eob.member.model.data.MemberEntity;
import com.eob.order.model.data.OrderHistoryEntity;
import com.eob.order.model.data.OrderStatus;
import com.eob.shop.model.data.ProductEntity;
import com.eob.common.util.StringUtil;

import lombok.Data;

@Data
public class OrderHistoryResponseDTO {

    private OrderStatus status;
    private int orderPrice;
    private String orderPhone;
    private String orderAddress;
    private String orderRequest;

    private OrderTimeDTO orderTime;
    private List<OrderDetailDTO> orderDetail;

    // ===============================
    // 내부 static DTO
    // ===============================

    @Data
    public static class OrderDetailDTO {
        private int quantity;
        private ProductDTO product;
    }

    @Data
    public static class ProductDTO {
        private String productName;
        private Long price;
    }

    @Data
    public static class OrderTimeDTO {
        private LocalDateTime requestedAt;
        private LocalDateTime assignedAt;
        private LocalDateTime pickupAt;
        private LocalDateTime completedAt;
    }

    // ===============================
    // Entity → DTO 변환
    // ===============================

    public static OrderHistoryResponseDTO from(
            OrderHistoryEntity entity,
            MemberEntity viewer) {

        OrderHistoryResponseDTO dto = new OrderHistoryResponseDTO();

        dto.status = entity.getStatus();
        dto.orderPrice = entity.getOrderPrice();
        dto.orderAddress = entity.getOrderAddress();
        dto.orderRequest = entity.getOrderRequest();

        // 전화번호 처리
        if (entity.getRider() != null &&
                viewer.getMemberNo().equals(entity.getRider().getMemberNo())) {
            dto.orderPhone = StringUtil.formatPhone(entity.getOrderPhone());
        } else {
            dto.orderPhone = StringUtil.maskPhone(entity.getOrderPhone());
        }

        // 주문 시간 DTO
        if (entity.getOrderTime() != null) {
            OrderTimeDTO timeDTO = new OrderTimeDTO();
            timeDTO.requestedAt = entity.getOrderTime().getRequestedAt();
            timeDTO.assignedAt = entity.getOrderTime().getAssignedAt();
            timeDTO.pickupAt = entity.getOrderTime().getPickupAt();
            timeDTO.completedAt = entity.getOrderTime().getCompletedAt();
            dto.orderTime = timeDTO;
        }

        // 주문 상품 내역
        dto.orderDetail = entity.getOrderDetail().stream().map(detail -> {
            OrderDetailDTO d = new OrderDetailDTO();
            d.quantity = detail.getQuantity();

            ProductEntity p = detail.getProductNo();
            ProductDTO productDTO = new ProductDTO();
            productDTO.productName = p.getProductName();
            productDTO.price = p.getPrice();

            d.product = productDTO;
            return d;
        }).toList();

        return dto;
    }
}
