package org.youcancook.gobong.global.util.s3;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.BusinessException;

public class FileUploadException extends BusinessException {
    public FileUploadException() {
        super(ErrorCode.FAILED_TO_UPLOAD);
    }
}
