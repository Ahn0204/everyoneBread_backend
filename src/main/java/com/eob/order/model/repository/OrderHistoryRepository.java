package com.eob.order.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eob.order.model.data.OrderHistoryEntity;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, Long> {

}
