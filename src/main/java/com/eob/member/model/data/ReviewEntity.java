// package com.eob.member.model.data;

// import java.time.LocalDateTime;

// import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;

// import com.eob.member.model.data.MemberEntity;
// // import com.eob.member.model.data.OrderHistoryEntity;

// @Entity
// @Table(name = "REVIEW")
// @Getter
// @Setter
// public class ReviewEntity {

// /**
// * 후기 고유 번호 PK
// */
// @Id
// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_seq")
// @SequenceGenerator(name = "review_seq", sequenceName = "REVIEW_SEQ",
// allocationSize = 1)
// @Column(name = "REVIEW_NO")
// private Long reviewNo;

// /**
// * 회원 고유 번호 FK
// */
// @ManyToOne(fetch = FetchType.LAZY)
// @JoinColumn(name = "MEMBER_NO")
// private MemberEntity member;

// /**
// * 주문번호 (OrderHistory FK)
// */
// @ManyToOne(fetch = FetchType.LAZY)
// @JoinColumn(name = "ORDER_NO")
// // private OrderHistoryEntity order;

// /**
// * 별점 (1~5)
// */
// @Column(name = "RATING", nullable = false)
// private Integer rating;

// /**
// * 리뷰 내용
// */
// @Column(name = "CONTENT", length = 500)
// private String content;

// /**
// * 리뷰 상태 (POSTED / DELETED / PRIVATE)
// */
// @Enumerated(EnumType.STRING)
// @Column(name = "STATUS")
// private ReviewStatus status;

// /**
// * 작성 시각
// */
// @Column(name = "CREATED_AT")
// private LocalDateTime createdAt;

// @PrePersist
// protected void onCreate() {
// this.createdAt = LocalDateTime.now();
// if (this.status == null) {
// this.status = ReviewStatus.POSTED;
// }
// }
// }
