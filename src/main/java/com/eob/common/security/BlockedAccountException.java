package com.eob.common.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 회원 상태가 정지/탈퇴/대기 등으로 로그인 차단된 경우 발생시키는 예외
 */
public class BlockedAccountException extends AuthenticationException {

    public BlockedAccountException(String msg) {
        super(msg);
    }
}
