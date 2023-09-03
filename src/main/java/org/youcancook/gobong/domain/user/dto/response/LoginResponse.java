package org.youcancook.gobong.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.youcancook.gobong.global.util.token.TokenDto;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    public static LoginResponse of(TokenDto tokenDto) {
        return LoginResponse.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }
}
