package org.youcancook.gobong.global.error.exception;

import lombok.Getter;
import org.youcancook.gobong.global.error.ErrorCode;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode error;

    public BusinessException(ErrorCode e) {
        super(e.getMessage());
        this.error = e;
    }
}