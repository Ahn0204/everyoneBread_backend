package com.eob.rider.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eob.member.model.data.MemberEntity;
import com.eob.rider.model.data.ApprovalStatus;
import com.eob.rider.model.data.RiderEntity;

public interface RiderRepository extends JpaRepository<RiderEntity, Long> {

    Optional<RiderEntity> findByMember(MemberEntity member);

    /**
     * 라이더 aStatus별 조회
     * 예솔 추가
     */
    // 동일한 파라미터 값을 여러 개 받기 위해 In 키워드 사용, List타입 파라미터->메서드 사용시 List객체로 생성할 필요 있음
    ArrayList<RiderEntity> findByaStatusIn(List<ApprovalStatus> aStatus);

    /**
     * 전체 라이더 조회 - 페이징 객체 리턴
     * 예솔 추가
     */
    Page<RiderEntity> findAll(Pageable pageable);

    RiderEntity findByRiderNo(Long riderNo);
}
