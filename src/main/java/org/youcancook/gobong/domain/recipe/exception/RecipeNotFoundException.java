package org.youcancook.gobong.domain.recipe.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.NotFoundException;

public class RecipeNotFoundException extends NotFoundException {
    public RecipeNotFoundException() {
        super(ErrorCode.RECIPE_NOT_FOUND);
    }
}
