package org.youcancook.gobong.domain.rating.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.IllegalException;

public class IllegalRatingException extends IllegalException {
    public IllegalRatingException() {
        super(ErrorCode.RATING_RANGE_OUT_OF_BOUNDS);
    }
}
