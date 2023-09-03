package org.youcancook.gobong.domain.rating.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.NotFoundException;

public class RatingNotFoundException extends NotFoundException {
    public RatingNotFoundException() {
        super(ErrorCode.RATING_NOT_FOUND);
    }
}
