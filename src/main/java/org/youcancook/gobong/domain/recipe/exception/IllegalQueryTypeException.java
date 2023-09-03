package org.youcancook.gobong.domain.recipe.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.BusinessException;

public class IllegalQueryTypeException extends BusinessException {
    public IllegalQueryTypeException() {
        super(ErrorCode.INVALID_QUERY_TYPE);
    }
}
