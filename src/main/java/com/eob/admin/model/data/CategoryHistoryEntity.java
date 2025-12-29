package com.eob.admin.model.data;

import java.time.LocalDateTime;

import com.eob.member.model.data.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CATEGORY_HISTORY")
public class CategoryHistoryEntity {

    /**
     * 변경이력 고유번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_history_seq")
    @SequenceGenerator(name = "category_history_seq", sequenceName = "category_history_seq", allocationSize = 1)
    private long categoryHistoryNo;

    /**
     * 카테고리 고유번호
     */
    @ManyToOne
    @JoinColumn(name = "category_no")
    private CategoryEntity category;
    /**
     * 기존 카테고리명
     */
    @Column(nullable = false)
    private int categoryBefore;

    /**
     * 실행한 작업 - C / U / D
     */
    @Column(nullable = false)
    private String operation;

    /**
     * 변경 카테고리명
     */
    @Column(nullable = false)
    private int categoryAfter;
    /**
     * 작업자 멤버 번호
     */
    @ManyToOne
    @JoinColumn(name = "member_No")
    private MemberEntity member;

    /**
     * 변경 이력이 기록된 일시(LocalDateTime.now())
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
