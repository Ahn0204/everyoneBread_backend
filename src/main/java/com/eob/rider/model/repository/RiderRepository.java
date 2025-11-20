package com.eob.rider.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eob.member.model.data.MemberEntity;
import com.eob.rider.model.data.RiderEntity;

public interface RiderRepository extends JpaRepository<RiderEntity, Long> {

    Optional<RiderEntity> findByMember(MemberEntity member);

    
} 
