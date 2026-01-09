package com.eob.rider.model.data;

import java.time.LocalDateTime;

import com.eob.member.model.data.MemberEntity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "community_Emotions")

public class CommunityEmotionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_emotions_seq")
    @SequenceGenerator(name = "community_emotions_seq", sequenceName = "community_emotions_seq", allocationSize = 1)
    private int emotionsNo;

    @ManyToOne
    private RiderCommunityEntity community;

    @ManyToOne
    private MemberEntity member;

    private int emotionsType; // 1 : 좋아요 / 2: 싫어요

    private LocalDateTime createdAt;
}
