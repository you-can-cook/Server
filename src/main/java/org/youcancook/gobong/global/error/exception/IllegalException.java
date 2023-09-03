package org.youcancook.gobong.global.error.exception;

import org.youcancook.gobong.global.error.ErrorCode;

public class IllegalException extends BusinessException{
    public IllegalException() {
        super(ErrorCode.BAD_REQUEST);
    }

    public IllegalException(ErrorCode e) {
        super(e);
    }
}
