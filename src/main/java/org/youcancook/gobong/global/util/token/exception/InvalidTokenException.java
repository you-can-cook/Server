package org.youcancook.gobong.global.util.token.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
