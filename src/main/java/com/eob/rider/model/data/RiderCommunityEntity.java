package com.eob.rider.model.data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.eob.member.model.data.MemberEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "rider_Community")
public class RiderCommunityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rider_community_seq")
    @SequenceGenerator(name = "rider_community_seq", sequenceName = "rider_community_seq", allocationSize = 1)
    private Long communityNo;

    @Column(nullable = false)
    private String communityContent;

    private LocalDateTime createAt;

    @ManyToOne
    private MemberEntity member;

    @OneToMany(mappedBy = "community", cascade = CascadeType.REMOVE)
    private Set<CommunityEmotionsEntity> emotions = new HashSet<>();

    @Transient
    private int likeCount;

    @Transient
    private int unlikeCount;
}
