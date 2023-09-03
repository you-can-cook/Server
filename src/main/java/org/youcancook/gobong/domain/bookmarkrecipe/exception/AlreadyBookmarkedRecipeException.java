package org.youcancook.gobong.domain.bookmarkrecipe.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.IllegalException;

public class AlreadyBookmarkedRecipeException extends IllegalException {
    public AlreadyBookmarkedRecipeException() {
        super(ErrorCode.ALREADY_BOOKMARKED_RECIPE);
    }
}
