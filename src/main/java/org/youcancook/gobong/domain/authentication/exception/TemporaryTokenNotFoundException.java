package org.youcancook.gobong.domain.authentication.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.NotFoundException;

public class TemporaryTokenNotFoundException extends NotFoundException {
    public TemporaryTokenNotFoundException() {
        super(ErrorCode.TEMPORARY_TOKEN_NOT_FOUND);
    }
}
