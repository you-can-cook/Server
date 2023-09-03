package org.youcancook.gobong.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.youcancook.gobong.global.util.token.TokenDto;

@Getter
@Builder
@AllArgsConstructor
public class SignupResponse {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    public static SignupResponse from(TokenDto tokenDto) {
        return SignupResponse.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }
}
