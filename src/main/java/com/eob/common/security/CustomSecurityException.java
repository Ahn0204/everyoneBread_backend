package com.eob.common.security;

import org.springframework.security.authentication.DisabledException;

public class CustomSecurityException extends DisabledException {

    private final Long memberNo;
    private final Long riderNo;

    public CustomSecurityException(String msg, Long memberNo, Long riderNo) {
        super(msg);
        this.memberNo = memberNo;
        this.riderNo = riderNo;
    }

    public Long getMemberNo() {
        return memberNo;
    }

    public Long getRiderNo() {
        return riderNo;
    }
}
