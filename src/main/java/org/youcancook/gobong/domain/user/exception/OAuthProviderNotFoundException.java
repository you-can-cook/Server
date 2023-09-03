package org.youcancook.gobong.domain.user.exception;

import org.youcancook.gobong.global.error.ErrorCode;
import org.youcancook.gobong.global.error.exception.NotFoundException;

public class OAuthProviderNotFoundException extends NotFoundException {
    public OAuthProviderNotFoundException() {
        super(ErrorCode.OAUTH_PROVIDER_NOT_FOUND);
    }
}
