package com.eob.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eob.member.model.data.WithdrawHistoryEntity;

/**
 * 회원 탈퇴 이력 Repository
 */
@Repository
public interface WithdrawHistoryRepository extends JpaRepository<WithdrawHistoryEntity, Long> {
    
}