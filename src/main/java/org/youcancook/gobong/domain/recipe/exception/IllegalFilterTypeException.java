package org.youcancook.gobong.domain.recipe.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.BusinessException;

public class IllegalFilterTypeException extends BusinessException {
    public IllegalFilterTypeException() {
        super(ErrorCode.INVALID_FILTER_TYPE);
    }
}
