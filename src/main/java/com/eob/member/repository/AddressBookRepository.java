package com.eob.member.repository;

import com.eob.member.model.data.AddressBookEntity;
import com.eob.member.model.data.AddressStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressBookRepository
        extends JpaRepository<AddressBookEntity, Long> {

    /* 활성 배송지 목록 */
    List<AddressBookEntity> findByMemberNoAndStatus(
        Long memberNo,
        AddressStatus status
    );

    /* 기본 배송지 해제 */
    @Modifying
    @Query("""
        update AddressBookEntity a
           set a.isDefault = false
         where a.memberNo = :memberNo
    """)
    void clearDefaultByMember(@Param("memberNo") Long memberNo);
}
