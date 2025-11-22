package com.eob.common.security.admin;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eob.member.model.data.MemberEntity;
import com.eob.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 정보 조회
 */
@Service
@RequiredArgsConstructor
public class AdminDetailService implements UserDetailsService {
    
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
      
        //username에 해당하는 레코드 추출
        MemberEntity user = MemberRepository.findById(username);
    }

}
