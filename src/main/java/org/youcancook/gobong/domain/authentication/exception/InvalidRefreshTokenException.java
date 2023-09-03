package org.youcancook.gobong.domain.authentication.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.IllegalException;

public class InvalidRefreshTokenException extends IllegalException {
    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN);
    }
}
