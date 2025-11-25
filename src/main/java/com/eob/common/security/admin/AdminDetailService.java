package com.eob.common.security.admin;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eob.common.security.CustomSecurityDetail;
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
        
        System.out.println("로그인 아이디: "+username);
        //username에 해당하는 레코드 추출
        Optional<MemberEntity> _admin = memberRepository.findByLoginMember(username);
        if(_admin.isEmpty()){ //해당하는 계정이 없다면
            return null;
        }
        MemberEntity admin = _admin.get();
        //해당하는 계정이 있다면 
        return new CustomSecurityDetail(admin);
    }

}
