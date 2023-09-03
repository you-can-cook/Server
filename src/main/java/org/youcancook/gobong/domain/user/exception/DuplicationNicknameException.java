package org.youcancook.gobong.domain.user.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.IllegalException;

public class DuplicationNicknameException extends IllegalException {
    public DuplicationNicknameException() {
        super(ErrorCode.DUPLICATE_NICKNAME);
    }
}
