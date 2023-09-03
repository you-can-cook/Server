package org.youcancook.gobong.global.error.exception;

import org.youcancook.gobong.global.error.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }

    public NotFoundException(ErrorCode e) {
        super(e);
    }
}