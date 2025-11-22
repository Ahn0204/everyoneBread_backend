package com.eob.comm.security.admin;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



import lombok.RequiredArgsConstructor;

/**
 * 사용자 정보 조회
 */
@Service
@RequiredArgsConstructor
public class AdminDetailService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
      
        throw new UnsupportedOperationException("Unimplemented method 'loadUserByUsername'");
    }

}
