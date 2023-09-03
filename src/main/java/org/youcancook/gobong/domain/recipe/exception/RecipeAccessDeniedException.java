package org.youcancook.gobong.domain.recipe.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.BusinessException;

public class RecipeAccessDeniedException extends BusinessException {
    public RecipeAccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED);
    }
}
