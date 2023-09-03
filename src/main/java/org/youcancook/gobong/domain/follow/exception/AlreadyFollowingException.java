package org.youcancook.gobong.domain.follow.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.IllegalException;

public class AlreadyFollowingException extends IllegalException {
    public AlreadyFollowingException() {
        super(ErrorCode.ALREADY_FOLLOWING);
    }
}
