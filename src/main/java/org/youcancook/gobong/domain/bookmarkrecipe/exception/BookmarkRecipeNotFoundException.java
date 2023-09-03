package org.youcancook.gobong.domain.bookmarkrecipe.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.NotFoundException;

public class BookmarkRecipeNotFoundException extends NotFoundException {
    public BookmarkRecipeNotFoundException() {
        super(ErrorCode.BOOKMARK_NOT_FOUND);
    }
}
