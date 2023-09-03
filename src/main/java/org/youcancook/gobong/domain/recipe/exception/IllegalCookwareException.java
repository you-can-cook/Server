package org.youcancook.gobong.domain.recipe.exception;


import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.BusinessException;

public class IllegalCookwareException extends BusinessException {
    public IllegalCookwareException() {
        super(ErrorCode.ILLEGAL_COOKWARE);
    }
}
