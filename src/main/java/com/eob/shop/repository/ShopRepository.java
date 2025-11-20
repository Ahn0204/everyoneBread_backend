package com.eob.shop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eob.member.model.data.MemberEntity;
import com.eob.shop.model.data.ShopEntity;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {

    @Query("select s from ShopEntity s where s.member = :member")
    Optional<ShopEntity> loginShop(@Param("member") MemberEntity member);

}
