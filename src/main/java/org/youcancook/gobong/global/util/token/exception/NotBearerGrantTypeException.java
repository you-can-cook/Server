package org.youcancook.gobong.global.util.token.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.AuthenticationException;

public class NotBearerGrantTypeException extends AuthenticationException {
    public NotBearerGrantTypeException() {
        super(ErrorCode.NOT_BEARER_GRANT_TYPE);
    }
}
