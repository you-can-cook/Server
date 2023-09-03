package org.youcancook.gobong.domain.follow.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.IllegalException;

public class NotFollowingException extends IllegalException {
    public NotFollowingException() {
        super(ErrorCode.NOT_FOLLOWING);
    }
}
