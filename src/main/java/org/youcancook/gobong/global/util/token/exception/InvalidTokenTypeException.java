package org.youcancook.gobong.global.util.token.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.AuthenticationException;

public class InvalidTokenTypeException extends AuthenticationException {
    public InvalidTokenTypeException() {
        super(ErrorCode.INVALID_TOKEN_TYPE);
    }
}
