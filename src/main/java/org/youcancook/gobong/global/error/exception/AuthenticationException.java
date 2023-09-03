package org.youcancook.gobong.global.error.exception;

import org.youcancook.gobong.global.error.ErrorCode;

public class AuthenticationException extends BusinessException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
