package org.youcancook.gobong.global.util.token.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.AuthenticationException;

public class EmptyAuthorizationException extends AuthenticationException {
    public EmptyAuthorizationException() {
        super(ErrorCode.EMPTY_AUTHORIZATION);
    }
}
