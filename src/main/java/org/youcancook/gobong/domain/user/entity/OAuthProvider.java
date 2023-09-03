package org.youcancook.gobong.domain.user.entity;

import org.youcancook.gobong.domain.user.exception.OAuthProviderNotFoundException;

import java.util.Arrays;

public enum OAuthProvider {
    KAKAO, GOOGLE;

    public static OAuthProvider from(String provider) {
        return Arrays.stream(values())
                .filter(value -> value.toString().equalsIgnoreCase(provider))
                .findFirst()
                .orElseThrow(OAuthProviderNotFoundException::new);
    }
}
