package org.youcancook.gobong.domain.recipedetail.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.NotFoundException;

public class RecipeDetailNotFoundException extends NotFoundException {
    public RecipeDetailNotFoundException() {
        super(ErrorCode.RECIPE_DETAIL_NOT_FOUND);
    }
}
