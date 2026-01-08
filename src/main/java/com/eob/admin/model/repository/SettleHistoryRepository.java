package com.eob.admin.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eob.admin.model.data.SettleHistoryEntity;

public interface SettleHistoryRepository extends JpaRepository<SettleHistoryEntity, Long> {

    Page<SettleHistoryEntity> findAll(Pageable pageable);

}
