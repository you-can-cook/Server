package org.youcancook.gobong.domain.recipe.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.IllegalException;

public class IllegalDifficultyDescriptionException extends IllegalException {
    public IllegalDifficultyDescriptionException() {
        super(ErrorCode.INVALID_DIFFICULTY_ARGUMENT);
    }
}
