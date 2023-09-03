package org.youcancook.gobong.domain.rating.exception;

import lombok.Getter;
import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.IllegalException;

@Getter
public class RatingMyRecipeException extends IllegalException {
    public RatingMyRecipeException() {
        super(ErrorCode.RATE_ON_MY_RECIPE);
    }
}
